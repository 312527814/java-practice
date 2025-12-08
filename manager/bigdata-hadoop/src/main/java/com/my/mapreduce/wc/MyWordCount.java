package com.my.mapreduce.wc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class MyWordCount {



    //bin/hadoop command [genericOptions] [commandOptions]
    //    hadoop jar  ooxx.jar  ooxx   -D  ooxx=ooxx  inpath  outpath
    //  args :   2类参数  genericOptions   commandOptions
    //  人你有复杂度：  自己分析 args数组
    //
    private static final Logger logger = Logger.getLogger(MyMapper.class);
    public static void main(String[] args) throws Exception {

        BasicConfigurator.configure();

//        logger.info("ddddddd");
        Configuration conf = new Configuration(true);

//        conf.setLong("mapreduce.input.fileinputformat.split.minsize", Integer.MAX_VALUE);

        GenericOptionsParser parser = new GenericOptionsParser(conf, args);  //工具类帮我们把-D 等等的属性直接set到conf，会留下commandOptions
        String[] othargs = parser.getRemainingArgs();

        //让框架知道是windows异构平台运行
        conf.set("mapreduce.app-submission.cross-platform","true");

        //本地运行模式，不会把jar发到datanode
        conf.set("mapreduce.framework.name","local");
//        System.out.println(conf.get("mapreduce.framework.name"));
        // 异构平台运行，如果运行的是集群模式，需要将单独打包 将jar发到datanode



        Job job = Job.getInstance(conf);
//        job.setJar("F:\\xinjianwenjianjia\\java-practice\\manager\\bigdata-hadoop\\target\\bigdata-hadoop-1.0-SNAPSHOT.jar");


//        FileInputFormat.setMinInputSplitSize(job,2222);
//        job.setInputFormatClass(ooxx.class);






        //必须必须写的
        job.setJarByClass(MyWordCount.class);

        job.setJobName("mashibing");

        Path infile = new Path(othargs[0]);
        TextInputFormat.addInputPath(job, infile);

        Path outfile = new Path(othargs[1]);
        if (outfile.getFileSystem(conf).exists(outfile)) outfile.getFileSystem(conf).delete(outfile, true);
        TextOutputFormat.setOutputPath(job, outfile);
        System.out.println(conf.get("mapreduce.job.outputformat.class"));
        job.setMapperClass(MyMapper.class);
        // 设置mapper阶段输出的key和value类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);


        //设置reduce最终数据输出的key和value类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setReducerClass(MyReducer.class);

//        job.setNumReduceTasks(0);
        // Submit the job, then poll for progress until the job is complete
        job.waitForCompletion(true);

    }

}
