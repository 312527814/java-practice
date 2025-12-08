package com.my.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.concurrent.locks.LockSupport;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-09-09 15:17
 */
public class _2NIOServer {
    public static void main(String[] args) throws IOException {
        LinkedList<SocketChannel> clients = new LinkedList<>();
        ServerSocketChannel ss = ServerSocketChannel.open();
        ss.bind(new InetSocketAddress(9090));
        ss.configureBlocking(false); //重点  OS  NONBLOCKING!!!
        while (true) {
            SocketChannel client = ss.accept(); //不会阻塞？  -1NULL
            if (client == null) {
                System.out.println("null.....");
                LockSupport.parkNanos(1000000000*10);
            } else {
                client.configureBlocking(false);
                int port = client.socket().getPort();
                System.out.println("client...port: " + port);
                clients.add(client);
            }
            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);  //可以在堆里   堆外
            for (SocketChannel c : clients) {
                if(c.socket().isClosed()){
                    clients.remove(c);
                    continue;
                }

                //串行化！！！！  多线程！！
                int num = c.read(buffer);  // >0  -1  0   //不会阻塞
                if (num > 0) {
                    buffer.flip();
                    byte[] aaa = new byte[buffer.limit()];
                    buffer.get(aaa);

                    String b = new String(aaa);
                    System.out.println(c.socket().getPort() + " : " + b);
                    buffer.clear();

                    buffer.put("hell".getBytes());
                    buffer.flip();
                    c.write(buffer);
                    buffer.clear();
                    if(b.contains("Close")){
                        c.close();
//                        clients.remove(c);
                    }
                }



            }
        }
    }
}
