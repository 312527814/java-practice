package com.my.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NonBlockingFileRead {
    public static void main2(String[] args) {
        // 文件路径
        String filePath = "example.txt";

        // 要写入的内容和位置
        String content = "这是随机写入的内容";
        long position = 100; // 从文件的第100个字节开始写入

        // 使用RandomAccessFile进行随机写
        try (RandomAccessFile file = new RandomAccessFile(filePath, "rw")) {
            // 移动到指定位置
            file.seek(position);
            // 写入内容
            file.write(content.getBytes());
        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 文件路径
        String filePath = "E:\\example.txt";

        // 要写入的内容和位置
        String content = "这是通过FileChannel随机写入的内容";
        long position = 100; // 从文件的第100个字节开始写入

        // 使用FileChannel进行随机写
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw");
             FileChannel fileChannel = randomAccessFile.getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(content.getBytes().length);
            byteBuffer.put(content.getBytes());
            byteBuffer.flip();

            // 创建一个ByteBuffer
//            ByteBuffer byteBuffer = ByteBuffer.wrap(content.getBytes());
            // 移动到指定位置
            fileChannel.position(position);
            // 写入内容
            fileChannel.write(byteBuffer);
        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
        }
    }
}
