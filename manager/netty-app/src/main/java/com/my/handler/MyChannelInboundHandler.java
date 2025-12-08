package com.my.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

public class MyChannelInboundHandler extends SimpleChannelInboundHandler<String> {
    public MyChannelInboundHandler(){
        System.out.println("..........."+new Date());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("string:" + msg);
//        try{
//            MethodType mt= MethodType.methodType(void.class); //方法返回类型
//            MethodHandle mh = MethodHandles.lookup().findSpecial(ChannelInboundHandlerAdapter.class,"channelRead",mt,getClass());
//            mh.invoke(this);
//        }catch(Throwable e){
//            e.printStackTrace();
//        }

        ctx.writeAndFlush("服务器返回 收到,MyChannelInboundHandler:"+this);

        System.out.println("MyChannelInboundHandler:"+this);

        new ChannelInboundHandlerAdapter().channelRead(ctx,msg);
        //SimpleChannelInboundHandler
    }


}
