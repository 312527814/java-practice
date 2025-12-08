package com.my.file;

import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class OSFileIO {

    static String data = "rererwerererwerererweeeeeeeeeeee";
    static int betch = 1000000;
    static List<ByteBuffer> list = new ArrayList<>();


    public static void main(String[] args) throws Exception {

        for (int i = 0; i < 1000000000; i++) {
            Thread.sleep(1);
            ByteBuffer buf1 = ByteBuffer.allocateDirect(100);
//            byte[] bytes = (data + "\n").getBytes();
//            System.out.println(data);
//            buf1.put(bytes);
            for (int j = 0; j < 25; j++) {
                buf1.putInt(j);
            }
            list.add(buf1);
        }
        System.in.read();
    }


    //最基本的file写
    @Test
    public void testBasicFileIO() throws Exception {
        long startTime = System.currentTimeMillis();


        File file = new File("testBasicFileIOout.txt");

        FileOutputStream out = new FileOutputStream(file);
        int i = 0;
        while (i < betch) {
            byte[] data2 = (data + "\n").getBytes();
            out.write(data2);
            i++;
        }

        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime);

        System.out.println("testBasicFileIO  time " + usedTime);

    }

    //测试buffer文件IO
    //  jvm  8kB   syscall  write(8KBbyte[])
    @Test
    public void testBufferedFileIO() throws Exception {
        long startTime = System.currentTimeMillis();
        File file = new File("testBufferedFileIOout.txt");
        byte[] data1 = (data + "\n").getBytes();
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file), 1024);

        int i = 0;
        while (i < betch) {
            out.write(data1);
            i++;
        }

        out.flush();

        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime);

        System.out.println("testBufferedFileIO  time " + usedTime);
    }


    //测试文件NIO
    @Test
    public void testNIO() throws Exception {

        long startTime = System.currentTimeMillis();
        RandomAccessFile raf = new RandomAccessFile("nio.txt", "rw");

        byte[] bytes = (data + "\n").getBytes();
        FileChannel rafchannel = raf.getChannel();
        //mmap  堆外  和文件映射的   byte  not  objtect
        MappedByteBuffer map = rafchannel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length * betch);
        int i = 0;
        while (i < betch) {
            map.put(bytes);
            i++;
        }//不是系统调用  但是数据会到达 内核的pagecache
        //曾经我们是需要out.write()  这样的系统调用，才能让程序的data 进入内核的pagecache
        //曾经必须有用户态内核态切换
        //mmap的内存映射，依然是内核的pagecache体系所约束的！！！
        //换言之，丢数据
        //你可以去github上找一些 其他C程序员写的jni扩展库，使用linux内核的Direct IO
        //直接IO是忽略linux的pagecache
        //是把pagecache  交给了程序自己开辟一个字节数组当作pagecache，动用代码逻辑来维护一致性/dirty。。。一系列复杂问题
        raf.close();
        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime);

        System.out.println("testRandomAccessFileWrite  time " + usedTime);


    }

    /*
     * map 专用模式 Mode for a private (copy-on-write) mapping.
     * 私有模式来说，对结果缓冲区的修改将不会被广播到文件并且也不会对其他映射了相同文件的程序可见。取而代之的是，
     * 它将导致被修改部分缓冲区独自拷贝一份到用户空间。这便是OS的“copy on write”原则。
     * */
    @Test
    public void testNIO2() throws Exception {

        long startTime = System.currentTimeMillis();
        RandomAccessFile raf = new RandomAccessFile("nio2.txt", "rw");

        byte[] bytes = (data + "\n").getBytes();
        FileChannel rafchannel = raf.getChannel();
        {
            //mmap  堆外  和文件映射的   byte  not  objtect
            MappedByteBuffer map = rafchannel.map(FileChannel.MapMode.PRIVATE, 0, bytes.length * betch);
            int i = 0;
            while (i < betch) {
                map.put(bytes);
                i++;
            }//不是系统调用  但是数据会到达 内核的pagecache
            //曾经我们是需要out.write()  这样的系统调用，才能让程序的data 进入内核的pagecache
            //曾经必须有用户态内核态切换
            //mmap的内存映射，依然是内核的pagecache体系所约束的！！！
            //换言之，丢数据
            //你可以去github上找一些 其他C程序员写的jni扩展库，使用linux内核的Direct IO
            //直接IO是忽略linux的pagecache
            //是把pagecache  交给了程序自己开辟一个字节数组当作pagecache，动用代码逻辑来维护一致性/dirty。。。一系列复杂问题

            ByteBuffer buf1 = ByteBuffer.allocateDirect(100);
            while (rafchannel.read(buf1) > 0) {
                buf1.flip();
                byte[] bytes1 = new byte[buf1.limit()];
                buf1.get(bytes1);
                buf1.flip();
                String s = new String(bytes1);

                System.out.println(s);
                int a = 9;
            }
        }


        {
            //mmap  堆外  和文件映射的   byte  not  objtect
//            MappedByteBuffer map = rafchannel.map(FileChannel.MapMode.PRIVATE, 0, bytes.length * betch);
            ByteBuffer buf1 = ByteBuffer.allocate(100);
            while (rafchannel.read(buf1) > 0) {
                buf1.flip();
                byte[] bytes1 = new byte[buf1.limit()];
                buf1.get(bytes1);
                buf1.flip();
                String s = new String(bytes1);

                System.out.println(s);
                int a = 9;
            }
        }


        raf.close();
        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime);

        System.out.println("testRandomAccessFileWrite  time " + usedTime);


    }

    @Test
    public void testNIO3() throws Exception {

        long startTime = System.currentTimeMillis();
        RandomAccessFile raf = new RandomAccessFile("nio3.txt", "rw");
        raf.seek(raf.length());
        byte[] bytes = (data + "\n").getBytes();
        FileChannel rafchannel = raf.getChannel();
        int i = 0;
        ByteBuffer buf1 = ByteBuffer.allocateDirect(bytes.length);
        while (i < betch) {

            buf1.put(bytes);
            buf1.flip();
            rafchannel.write(buf1);
            buf1.flip();
            i++;
        }

        raf.close();
        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime);

        System.out.println("testRandomAccessFileWrite  time " + usedTime);


    }
}

