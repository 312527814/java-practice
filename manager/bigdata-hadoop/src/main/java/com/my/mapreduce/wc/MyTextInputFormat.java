package com.my.mapreduce.wc;

import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.*;
import org.apache.hadoop.mapred.LocatedFileStatusFetcher;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.security.TokenCache;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyTextInputFormat extends InputFormat<LongWritable, Text> {

    public static final String INPUT_DIR =
            "mapreduce.input.fileinputformat.inputdir";
    public static final String SPLIT_MAXSIZE =
            "mapreduce.input.fileinputformat.split.maxsize";
    public static final String SPLIT_MINSIZE =
            "mapreduce.input.fileinputformat.split.minsize";
    public static final String PATHFILTER_CLASS =
            "mapreduce.input.pathFilter.class";
    public static final String NUM_INPUT_FILES =
            "mapreduce.input.fileinputformat.numinputfiles";
    public static final String INPUT_DIR_RECURSIVE =
            "mapreduce.input.fileinputformat.input.dir.recursive";
    public static final String LIST_STATUS_NUM_THREADS =
            "mapreduce.input.fileinputformat.list-status.num-threads";
    public static final int DEFAULT_LIST_STATUS_NUM_THREADS = 1;

    private static final Log LOG = LogFactory.getLog(MyTextInputFormat.class);

    private static final double SPLIT_SLOP = 1.1;   // 10% slop

    protected boolean isSplitable(JobContext context, Path filename) {
        return true;
    }

    protected long computeSplitSize(long blockSize, long minSize,
                                    long maxSize) {
        return Math.max(minSize, Math.min(maxSize, blockSize));
    }

    protected int getBlockIndex(BlockLocation[] blkLocations,
                                long offset) {
        for (int i = 0 ; i < blkLocations.length; i++) {
            // is the offset inside this block?
            if ((blkLocations[i].getOffset() <= offset) &&
                    (offset < blkLocations[i].getOffset() + blkLocations[i].getLength())){
                return i;
            }
        }
        BlockLocation last = blkLocations[blkLocations.length -1];
        long fileLength = last.getOffset() + last.getLength() -1;
        throw new IllegalArgumentException("Offset " + offset +
                " is outside of file (0.." +
                fileLength + ")");
    }
    protected FileSplit makeSplit(Path file, long start, long length,
                                  String[] hosts, String[] inMemoryHosts) {
        return new FileSplit(file, start, length, hosts, inMemoryHosts);
    }
    protected FileSplit makeSplit(Path file, long start, long length,
                                  String[] hosts) {
        return new FileSplit(file, start, length, hosts);
    }
    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException, InterruptedException {
        Stopwatch sw = new Stopwatch().start();
        long minSize = Math.max(getFormatMinSplitSize(), getMinSplitSize(job));
        long maxSize = getMaxSplitSize(job);

        // generate splits
        List<InputSplit> splits = new ArrayList<InputSplit>();
        List<FileStatus> files = listStatus(job);
        for (FileStatus file: files) {
            Path path = file.getPath();
            long length = file.getLen();
            if (length != 0) {
                BlockLocation[] blkLocations;
                if (file instanceof LocatedFileStatus) {
                    blkLocations = ((LocatedFileStatus) file).getBlockLocations();
                } else {
                    FileSystem fs = path.getFileSystem(job.getConfiguration());
                    blkLocations = fs.getFileBlockLocations(file, 0, length);
                }
                if (isSplitable(job, path)) {
                    long blockSize = file.getBlockSize();
                    long splitSize = computeSplitSize(blockSize, minSize, maxSize);

                    long bytesRemaining = length;
                    while (((double) bytesRemaining)/splitSize > SPLIT_SLOP) {
                        int blkIndex = getBlockIndex(blkLocations, length-bytesRemaining);
                        splits.add(makeSplit(path, length-bytesRemaining, splitSize,
                                blkLocations[blkIndex].getHosts(),
                                blkLocations[blkIndex].getCachedHosts()));
                        bytesRemaining -= splitSize;
                    }

                    if (bytesRemaining != 0) {
                        int blkIndex = getBlockIndex(blkLocations, length-bytesRemaining);
                        splits.add(makeSplit(path, length-bytesRemaining, bytesRemaining,
                                blkLocations[blkIndex].getHosts(),
                                blkLocations[blkIndex].getCachedHosts()));
                    }
                } else { // not splitable
                    splits.add(makeSplit(path, 0, length, blkLocations[0].getHosts(),
                            blkLocations[0].getCachedHosts()));
                }
            } else {
                //Create empty hosts array for zero length files
                splits.add(makeSplit(path, 0, length, new String[0]));
            }
        }
        // Save the number of input files for metrics/loadgen
        job.getConfiguration().setLong(NUM_INPUT_FILES, files.size());
        sw.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Total # of splits generated by getSplits: " + splits.size()
                    + ", TimeTaken: " + sw.elapsedMillis());
        }
        return splits;
    }

    private static final PathFilter hiddenFileFilter = new PathFilter(){
        public boolean accept(Path p){
            String name = p.getName();
            return !name.startsWith("_") && !name.startsWith(".");
        }
    };

    protected List<FileStatus> listStatus(JobContext job
    ) throws IOException {
        Path[] dirs = getInputPaths(job);
        if (dirs.length == 0) {
            throw new IOException("No input paths specified in job");
        }

        // get tokens for all the required FileSystems..
        TokenCache.obtainTokensForNamenodes(job.getCredentials(), dirs,
                job.getConfiguration());

        // Whether we need to recursive look into the directory structure
        boolean recursive = getInputDirRecursive(job);

        // creates a MultiPathFilter with the hiddenFileFilter and the
        // user provided one (if any).
        List<PathFilter> filters = new ArrayList<PathFilter>();
        filters.add(hiddenFileFilter);
        PathFilter jobFilter = getInputPathFilter(job);
        if (jobFilter != null) {
            filters.add(jobFilter);
        }
        PathFilter inputFilter = new MyTextInputFormat.MultiPathFilter(filters);

        List<FileStatus> result = null;

        int numThreads = job.getConfiguration().getInt(LIST_STATUS_NUM_THREADS,
                DEFAULT_LIST_STATUS_NUM_THREADS);
        Stopwatch sw = new Stopwatch().start();
        if (numThreads == 1) {
            result = singleThreadedListStatus(job, dirs, inputFilter, recursive);
        } else {
            Iterable<FileStatus> locatedFiles = null;
            try {
                LocatedFileStatusFetcher locatedFileStatusFetcher = new LocatedFileStatusFetcher(
                        job.getConfiguration(), dirs, recursive, inputFilter, true);
                locatedFiles = locatedFileStatusFetcher.getFileStatuses();
            } catch (InterruptedException e) {
                throw new IOException("Interrupted while getting file statuses");
            }
            result = Lists.newArrayList(locatedFiles);
        }

        sw.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Time taken to get FileStatuses: " + sw.elapsedMillis());
        }
        LOG.info("Total input paths to process : " + result.size());
        return result;
    }

    public static PathFilter getInputPathFilter(JobContext context) {
        Configuration conf = context.getConfiguration();
        Class<?> filterClass = conf.getClass(PATHFILTER_CLASS, null,
                PathFilter.class);
        return (filterClass != null) ?
                (PathFilter) ReflectionUtils.newInstance(filterClass, conf) : null;
    }

    public static boolean getInputDirRecursive(JobContext job) {
        return job.getConfiguration().getBoolean(INPUT_DIR_RECURSIVE,
                false);
    }

    private List<FileStatus> singleThreadedListStatus(JobContext job, Path[] dirs,
                                                      PathFilter inputFilter, boolean recursive) throws IOException {
        List<FileStatus> result = new ArrayList<FileStatus>();
        List<IOException> errors = new ArrayList<IOException>();
        for (int i=0; i < dirs.length; ++i) {
            Path p = dirs[i];
            FileSystem fs = p.getFileSystem(job.getConfiguration());
            FileStatus[] matches = fs.globStatus(p, inputFilter);
            if (matches == null) {
                errors.add(new IOException("Input path does not exist: " + p));
            } else if (matches.length == 0) {
                errors.add(new IOException("Input Pattern " + p + " matches 0 files"));
            } else {
                for (FileStatus globStat: matches) {
                    if (globStat.isDirectory()) {
                        RemoteIterator<LocatedFileStatus> iter =
                                fs.listLocatedStatus(globStat.getPath());
                        while (iter.hasNext()) {
                            LocatedFileStatus stat = iter.next();
                            if (inputFilter.accept(stat.getPath())) {
                                if (recursive && stat.isDirectory()) {
                                    addInputPathRecursively(result, fs, stat.getPath(),
                                            inputFilter);
                                } else {
                                    result.add(stat);
                                }
                            }
                        }
                    } else {
                        result.add(globStat);
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            throw new InvalidInputException(errors);
        }
        return result;
    }

    protected void addInputPathRecursively(List<FileStatus> result,
                                           FileSystem fs, Path path, PathFilter inputFilter)
            throws IOException {
        RemoteIterator<LocatedFileStatus> iter = fs.listLocatedStatus(path);
        while (iter.hasNext()) {
            LocatedFileStatus stat = iter.next();
            if (inputFilter.accept(stat.getPath())) {
                if (stat.isDirectory()) {
                    addInputPathRecursively(result, fs, stat.getPath(), inputFilter);
                } else {
                    result.add(stat);
                }
            }
        }
    }
    public static Path[] getInputPaths(JobContext context) {
        String dirs = context.getConfiguration().get(INPUT_DIR, "");
        String [] list = StringUtils.split(dirs);
        Path[] result = new Path[list.length];
        for (int i = 0; i < list.length; i++) {
            result[i] = new Path(StringUtils.unEscapeString(list[i]));
        }
        return result;
    }

    public static long getMaxSplitSize(JobContext context) {
        return context.getConfiguration().getLong(SPLIT_MAXSIZE,
                Long.MAX_VALUE);
    }

    protected long getFormatMinSplitSize() {
        return 1;
    }

    public static long getMinSplitSize(JobContext job) {
        return job.getConfiguration().getLong(SPLIT_MINSIZE, 1L);
    }

    @Override
    public RecordReader createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        String delimiter = context.getConfiguration().get(
                "textinputformat.record.delimiter");
        byte[] recordDelimiterBytes = null;
        if (null != delimiter)
            recordDelimiterBytes = delimiter.getBytes(Charsets.UTF_8);
        return new TransformerRecordReader(recordDelimiterBytes);
    }

    public static  class  TransformerRecordReader extends RecordReader<LongWritable, Text>{
        public TransformerRecordReader(byte[] recordDelimiter) {
            this.recordDelimiterBytes = recordDelimiter;
        }

        private static final Log LOG = LogFactory.getLog(LineRecordReader.class);
        public static final String MAX_LINE_LENGTH =
                "mapreduce.input.linerecordreader.line.maxlength";

        private long start;
        private long pos;
        private long end;
        private SplitLineReader in;
        private FSDataInputStream fileIn;
        private Seekable filePosition;
        private int maxLineLength;
        private LongWritable key;
        private Text value;
        private boolean isCompressedInput;
        private Decompressor decompressor;
        private byte[] recordDelimiterBytes;

        @Override
        public void initialize(InputSplit genericSplit, TaskAttemptContext context) throws IOException, InterruptedException {
            FileSplit split = (FileSplit) genericSplit;
            Configuration job = context.getConfiguration();
            this.maxLineLength = job.getInt(MAX_LINE_LENGTH, Integer.MAX_VALUE);
            start = split.getStart();
            end = start + split.getLength();
            final Path file = split.getPath();

            // open the file and seek to the start of the split
            final FileSystem fs = file.getFileSystem(job);
            fileIn = fs.open(file);

            CompressionCodec codec = new CompressionCodecFactory(job).getCodec(file);
            if (null!=codec) {
                isCompressedInput = true;
                decompressor = CodecPool.getDecompressor(codec);
                if (codec instanceof SplittableCompressionCodec) {
                    final SplitCompressionInputStream cIn =
                            ((SplittableCompressionCodec)codec).createInputStream(
                                    fileIn, decompressor, start, end,
                                    SplittableCompressionCodec.READ_MODE.BYBLOCK);
                    in = new CompressedSplitLineReader(cIn, job,
                            this.recordDelimiterBytes);
                    start = cIn.getAdjustedStart();
                    end = cIn.getAdjustedEnd();
                    filePosition = cIn;
                } else {
                    in = new SplitLineReader(codec.createInputStream(fileIn,
                            decompressor), job, this.recordDelimiterBytes);
                    filePosition = fileIn;
                }
            } else {
                fileIn.seek(start);
                in = new UncompressedSplitLineReader(
                        fileIn, job, this.recordDelimiterBytes, split.getLength());
                filePosition = fileIn;
            }
            // If this is not the first split, we always throw away first record
            // because we always (except the last split) read one extra line in
            // next() method.
            if (start != 0) {
                start += in.readLine(new Text(), 0, maxBytesToConsume(start));
            }
            this.pos = start;
        }

        @Override
        public boolean nextKeyValue() throws IOException, InterruptedException {
            if (key == null) {
                key = new LongWritable();
            }
            key.set(pos);
            if (value == null) {
                value = new Text();
            }
            int newSize = 0;
            // We always read one extra line, which lies outside the upper
            // split limit i.e. (end - 1)
            while (getFilePosition() <= end || in.needAdditionalRecordAfterSplit()) {
                if (pos == 0) {
                    newSize = skipUtfByteOrderMark();
                } else {
                    newSize = in.readLine(value, maxLineLength, maxBytesToConsume(pos));
                    pos += newSize;
                }

                if ((newSize == 0) || (newSize < maxLineLength)) {
                    break;
                }

                // line too long. try again
                LOG.info("Skipped line of size " + newSize + " at pos " +
                        (pos - newSize));
            }
            if (newSize == 0) {
                key = null;
                value = null;
                return false;
            } else {
                return true;
            }
        }

        @Override
        public LongWritable getCurrentKey() throws IOException, InterruptedException {
            return key;
        }

        @Override
        public Text getCurrentValue() throws IOException, InterruptedException {
            return value;
        }

        @Override
        public float getProgress() throws IOException, InterruptedException {
            if (start == end) {
                return 0.0f;
            } else {
                return Math.min(1.0f, (getFilePosition() - start) / (float)(end - start));
            }
        }

        @Override
        public void close() throws IOException {
            try {
                if (in != null) {
                    in.close();
                }
            } finally {
                if (decompressor != null) {
                    CodecPool.returnDecompressor(decompressor);
                }
            }
        }

        private int maxBytesToConsume(long pos) {
            return isCompressedInput
                    ? Integer.MAX_VALUE
                    : (int) Math.max(Math.min(Integer.MAX_VALUE, end - pos), maxLineLength);
        }

        private long getFilePosition() throws IOException {
            long retVal;
            if (isCompressedInput && null != filePosition) {
                retVal = filePosition.getPos();
            } else {
                retVal = pos;
            }
            return retVal;
        }

        private int skipUtfByteOrderMark() throws IOException {
            // Strip BOM(Byte Order Mark)
            // Text only support UTF-8, we only need to check UTF-8 BOM
            // (0xEF,0xBB,0xBF) at the start of the text stream.
            int newMaxLineLength = (int) Math.min(3L + (long) maxLineLength,
                    Integer.MAX_VALUE);
            int newSize = in.readLine(value, newMaxLineLength, maxBytesToConsume(pos));
            // Even we read 3 extra bytes for the first line,
            // we won't alter existing behavior (no backwards incompat issue).
            // Because the newSize is less than maxLineLength and
            // the number of bytes copied to Text is always no more than newSize.
            // If the return size from readLine is not less than maxLineLength,
            // we will discard the current line and read the next line.
            pos += newSize;
            int textLength = value.getLength();
            byte[] textBytes = value.getBytes();
            if ((textLength >= 3) && (textBytes[0] == (byte)0xEF) &&
                    (textBytes[1] == (byte)0xBB) && (textBytes[2] == (byte)0xBF)) {
                // find UTF-8 BOM, strip it.
                LOG.info("Found UTF-8 BOM and skipped it");
                textLength -= 3;
                newSize -= 3;
                if (textLength > 0) {
                    // It may work to use the same buffer and not do the copyBytes
                    textBytes = value.copyBytes();
                    value.set(textBytes, 3, textLength);
                } else {
                    value.clear();
                }
            }
            return newSize;
        }


    }

    private static class MultiPathFilter implements PathFilter {
        private List<PathFilter> filters;

        public MultiPathFilter(List<PathFilter> filters) {
            this.filters = filters;
        }

        public boolean accept(Path path) {
            for (PathFilter filter : filters) {
                if (!filter.accept(path)) {
                    return false;
                }
            }
            return true;
        }
    }
}
