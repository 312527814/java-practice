package com.my.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-09-13 10:21
 */
public class _3MultiplexingSingleThreadServer {
    private static Selector selector;

    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(80));
        ServerSocket socket = server.socket();
        server.configureBlocking(false);
        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("start........");

        while (true) {
            while (selector.select() > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    if (next.isAcceptable()) {
                        serverHanlderAccept(next);
                    } else if (next.isReadable()) {
                        serverHanlderRead(next);
                    } else if (next.isWritable()) {
                    }
                }

            }
        }
    }

    public static void serverHanlderAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel client = ssc.accept();
        System.out.println("client:" + client.getRemoteAddress());

        client.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        client.register(selector, SelectionKey.OP_READ, buffer);
    }

    public static void serverHanlderRead(SelectionKey key) throws IOException {


            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = (ByteBuffer) key.attachment();
        try {
            buffer.clear();
            client.read(buffer);
            buffer.flip();
            byte[] bytes = new byte[buffer.limit()];
            buffer.get(bytes);

            System.out.println(client.getRemoteAddress() + ":" + new String(bytes));

            if (new String(bytes).equals("bye")) {
                client.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            client.close();
        }

//        Scanner sc = new Scanner(System.in);
//        String next = sc.nextLine();
//        buffer.clear();
//        buffer.put(next.getBytes());
//        buffer.flip();
//        client.write(buffer);
//        client.register(selector, SelectionKey.OP_WRITE);
//        client.close();
//        client.register(selector, SelectionKey.OP_WRITE, buffer);

    }
}
