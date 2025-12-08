package com.my.mapreduce.wc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MyTextOutputFormat<K, V>  extends OutputFormat<K, V> {
    static String outputDir=null;
    public static void setOutputPath(Path outputPath) {
        outputDir=outputPath.toString();
    }
    @Override
    public RecordWriter getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new TransformerRecordWriter(outputDir);
    }

    @Override
    public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {

    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new FileOutputCommitter(FileOutputFormat.getOutputPath(context), context);
    }

    /**
     * 自定义具体数据输出writer
     *
     * @author root
     *
     */
    public class TransformerRecordWriter extends RecordWriter<Text, IntWritable> {


        private static final String utf8 = "UTF-8";
        private FileSystem fs = null;
        private FSDataOutputStream output=null;
        public TransformerRecordWriter(String outFilePath) throws IOException, InterruptedException {
            Configuration conf = new Configuration(true);//true
            //去环境变量 HADOOP_USER_NAME  god
            fs = FileSystem.get(URI.create("hdfs://192.168.16.133:9000/"), conf, "root");

            if (!outFilePath.startsWith("/", outFilePath.length() - 1)) {
                outFilePath = outFilePath + "/";
            }
            Path outfile = new Path(outFilePath+ UUID.randomUUID().toString()+".txt");

            output = fs.create(outfile);
        }

        @Override
        /**
         * 当reduce任务输出数据是，由计算框架自动调用。把reducer输出的数据写到mysql中
         */
        public void write(Text key, IntWritable value) throws IOException, InterruptedException {
            output.write("\r\n".getBytes());
            output.write(key.getBytes());
            output.write(" ".getBytes());
            output.write(value.toString().getBytes(utf8));
        }

        @Override
        public void close(TaskAttemptContext context) throws IOException, InterruptedException {
            output.close();
            fs.close();
        }

    }
}
