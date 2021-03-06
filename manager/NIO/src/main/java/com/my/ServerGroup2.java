package com.my;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerGroup2 {
    public static void main(String[] args) {
        ServerGroup2 main = new ServerGroup2("main");
        ServerGroup2 work = new ServerGroup2("work");
        main.setWorkGroup(work);
        main.bind(8089);
        main.start();
    }

    private int port = 8080;

    private Selector selector;

    private final String name;

    private LinkedBlockingQueue<SocketChannel> channels = new LinkedBlockingQueue<>();

    public Selector getSelector() {
        return selector;
    }

    public void setWorkGroup(ServerGroup2 workGroup) {
        this.workGroup = workGroup;
    }

    private ServerGroup2 workGroup;

    public ServerGroup2(String name) {
        this.name = name;
        try {
            workGroup = this;
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bind(int port) {
        ServerSocketChannel ssc = null;
        try {
            if (port > 0)
                this.port = port;
            /* 获取通道 */
            ssc = ServerSocketChannel.open();
            /* 配置非阻塞 */
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress("localhost", this.port));
            /* 将通道注册到选择器 */
            ssc.register(this.selector, SelectionKey.OP_ACCEPT);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void start() {
        new Thread(this::doSelect).start();

        if (this.workGroup != this) {
            this.workGroup.start();
        }

        System.out.println(name + "  start....");
    }

    private void doSelect() {
        while (true) {
            try {
                while (this.selector.select() > 0) {
                    System.out.println(this.name + "  获取到 key...");
                    Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    if (iterator.hasNext()) {
                        SelectionKey next = iterator.next();
                        iterator.remove();
                        process(next);
                    }
                }

                while (true) {
                    SocketChannel channel = channels.poll();
                    if (channel == null) {
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void register(Channel channel) {
        if (channel instanceof ServerSocketChannel) {
            this.channels.add((SocketChannel) channel);
        } else {
            this.workGroup.channels.add((SocketChannel) channel);
        }

    }

    private void process(SelectionKey next) {
        if (next.isAcceptable()) {
            acceptHandler(next);
        } else if (next.isWritable()) {
            writeHandler(next);
        } else if (next.isReadable()) {
            readeHandler(next);
        }
    }

    private void readeHandler(SelectionKey selectKey) {
        SocketChannel channel = (SocketChannel) selectKey.channel();
        try {


            // 创建读取的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(100);
            channel.read(buffer);
            byte[] data = buffer.array();
            String msg = new String(data).trim();

            System.out.println("groupName:" + name + ", 线程Id:" + Thread.currentThread().getId() + "：客户端：" + msg + ":" + channel.getRemoteAddress());

            String s = channel.getRemoteAddress() + "服务器返回 你好," + msg + "\r\n";
            if (msg.equals("beybey")) {
                s = "beybey";
            }
            ByteBuffer outBuffer = ByteBuffer.wrap(s.getBytes());
            // 将消息回送给客户端
            channel.write(outBuffer);
            if (msg.equals("beybey")) {
                System.out.print(channel.getRemoteAddress() + ":客户端下线");
                channel.close();
            }
        } catch (Exception e) {
            try {
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void writeHandler(SelectionKey next) {
    }

    private void acceptHandler(SelectionKey selectKey) {
        ServerSocketChannel ssc = null;
        try {
            ssc = (ServerSocketChannel) selectKey
                    .channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);

            System.out.println("groupName:" + name + ", accept：客户端：" + sc.getRemoteAddress());
            sc.register(this.workGroup.getSelector(), SelectionKey.OP_READ);
        } catch (Exception e) {
            if (ssc != null) {
                try {
                    ssc.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


}
