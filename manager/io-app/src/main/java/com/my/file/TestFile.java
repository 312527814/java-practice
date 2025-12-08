package com.my.file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class TestFile {
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            new Thread(()->{
                String filePath = finalI +"example.txt";
                write(filePath);
            }).start();
        }
    }



    public static void write(String filePath) {
        // 要写入的内容和位置
        String content = "这是内容";
        long position = 0; // 从文件的第100个字节开始写入
        long size = 1024 * 1024 * 100; // 映射文件的大小为100MB
//        ByteBuffer byteBuffer = MappedByteBuffer.allocateDirect(1024 * 10);

        ByteBuffer buffer = ByteBuffer.allocateDirect(1024); // 分配1024字节的堆外内存



        // 使用buffer...
        for (int i = 0; i < 1024; i++) {
            buffer.put((byte)1); // 写入数据
        }

        buffer.flip(); // 切换为读模式
        byte b = buffer.get(); // 读取数据
        System.out.println(b);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw");
             FileChannel fileChannel = randomAccessFile.getChannel()) {

            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, position, size);
            int num = 0;

            while (true) {
                // 创建要写入的内容
                String date = ++num + content + new Date() + "\r\n";
                byte[] bytes = date.getBytes();

                // 检查是否有足够的空间写入数据
                if ((size - mappedByteBuffer.position()) < bytes.length) {
                    System.err.println(Thread.currentThread().getName() + "MappedByteBuffer 空间不足，停止写入.");
                    break;
                }

                // 写入内容到缓冲区
                mappedByteBuffer.put(bytes);
                System.out.println(Thread.currentThread().getName() + "写入文件: " + date);

//                Thread.sleep(1); // 延迟一秒
            }
        } catch (Exception e) {
            System.err.println("写入文件时出错: " + e.getMessage());
        }
//        reader(filePath);
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void reader(String filePath) {
        int chunkSize = 1024; // 每次读取1MB

        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
             FileChannel fileChannel = file.getChannel()) {

            long fileSize = fileChannel.size();
            long position = 0;

            // 映射整个文件到内存
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);

            // 循环读取文件内容
            while (position < fileSize) {
                // 计算本次要读取的大小
                long remaining = fileSize - position;
                int sizeToRead = (int) Math.min(chunkSize, remaining);

                // 创建字节数组来存储本次读取的数据
                byte[] bytes = new byte[sizeToRead];
                for (int i = 0; i < sizeToRead; i++) {
                    if (buffer.hasRemaining()) {
                        bytes[i] = buffer.get();
                    } else {
                        break;
                    }
                }

                // 将字节数组转换为字符串并输出
                String content = new String(bytes, StandardCharsets.UTF_8);
                System.out.print(Thread.currentThread().getName() + "read:" + content);

                // 更新位置
                position += sizeToRead;
            }

            System.out.println(Thread.currentThread().getName() + filePath + " reader end");
            new CountDownLatch(1).await();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

