package com.my.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class TestFile2 {
    static ArrayList<ByteBuffer> list=  new ArrayList<ByteBuffer>();
    public static void main(String[] args) throws Exception {
//        for (int i = 0; i <30 ; i++) {
//
//        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(1024*1024*21); // 分配1024字
        list.add(buffer);
        System.out.println("end");


        new CountDownLatch(1).await();
    }




}

