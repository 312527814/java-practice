package com.my.mapreduce.wc;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.StringTokenizer;

public class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    //hadoop框架中，它是一个分布式  数据 ：序列化、反序列化
    //hadoop有自己一套可以序列化、反序列化
    //或者自己开发类型必须：实现序列化，反序列化接口，实现比较器接口
    //排序 -》  比较  这个世界有2种顺序：  8  11，    字典序、数值顺序

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    private final Logger logger = Logger.getLogger(MyMapper.class);

    //hello hadoop 1
    //hello hadoop 2
    //TextInputFormat
    //key  是每一行字符串自己第一个字节面向源文件的偏移量
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        this.logger.info("MyMapper_key:" + key);


        StringTokenizer itr = new StringTokenizer(value.toString());
        while (itr.hasMoreTokens()) {
            word.set(itr.nextToken());
            context.write(word, one);
        }
    }

}
