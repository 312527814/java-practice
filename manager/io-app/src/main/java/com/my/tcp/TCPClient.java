package com.my.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-09-09 14:52
 */
public class TCPClient {
    public static void main1(String[] args) {
        for (int i = 0; i < 10000; i++) {
            new Thread(()->{
                ss();
            }).start();
        }


        System.out.println("end");
    }

    public static void ss(){

//            String serverHost = "192.168.126.128";
            String serverHost = "127.0.0.1";
            Socket client = new Socket();
        try {
            client.connect(new InetSocketAddress(serverHost, 80));
            OutputStream outputStream = client.getOutputStream();
            String msg="mess";
            int i=0;

            while (i++<Integer.MAX_VALUE){
                String s = msg + i+"\n\n";;
                outputStream.write(s.getBytes());
//                System.out.println(s);
                Thread.sleep(5000);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }
    public static void main(String[] args) {
        try {
            String serverHost = "127.0.0.1";
            Socket client = new Socket();
            client.connect(new InetSocketAddress(serverHost, 9090));
            OutputStream outputStream = client.getOutputStream();
            String msg="GET /sse/data1 HTTP/1.1\n" +
                    "Host: 127.0.0.1\n" +
                    "Content-Type: application/x-www-form-urlencoded\n" +
                    "Content-Length: 113\n" +
                    "Connection: Close";
            msg += "\n\n";
            outputStream.write(msg.getBytes());
//            System.in.read();
            byte[] bytes = new byte[1000];
            int read = 1;
            while (read > 0) {
                read = client.getInputStream().read(bytes);
                if (read == -1) {
                    client.close();
                    break;
                }
                System.out.println("response: " + new String(bytes, 0, read)+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main2(String[] args) {
        try {
            String serverHost = "127.0.0.1";
            Socket client = new Socket();
//            client.bind(new InetSocketAddress("localhost", 10001));
            client.connect(new InetSocketAddress(serverHost, 8099));
            OutputStream outputStream = client.getOutputStream();
            for (int j = 0; j < 1; j++) {
                outputStream.write("我是客户端".getBytes());
            }
            System.in.read();
            byte[] bytes = new byte[100];
            client.getInputStream().read(bytes);
            // outputStream.close();
            System.out.println("client_LocalPort\t" + client.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
