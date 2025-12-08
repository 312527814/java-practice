package com.my;

import com.my.handler.InboundA;
import com.my.handler.InboundB;
import com.my.handler.MyChannelInboundHandler;
import com.my.handler.OutboundC;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @program:
 * @description:
 * @author: liang.liu
 * @create: 2021-09-13 16:55
 */
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(4);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture channelFuture = serverBootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {

                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast("1",new InboundA());
                        ch.pipeline().addLast("5",new OutboundC());
                        ch.pipeline().addLast(new MyChannelInboundHandler());
                        ch.pipeline().addLast("6",new InboundB());
                    }
                })
                .bind(9999).sync();
//        ChannelFuture future = serverBootstrap.bind(9999).sync();
        // channelFuture.channel().closeFuture().sync();
    }
}
