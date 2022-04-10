package server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        SocketAddress socketAddress = channel.remoteAddress();
        System.out.println(socketAddress+"加入聊天");
        channelGroup.writeAndFlush(socketAddress+"加入聊天");
        channelGroup.add(channel);
}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println(ctx.channel().remoteAddress()+"说:"+msg.toString());
        channelGroup.forEach(channel -> {
            if (channel!=ctx.channel()){
                channel.writeAndFlush(ctx.channel().remoteAddress()+"说:"+msg.toString());
            }

        });


//        //定时任务
//        ctx.channel().eventLoop().schedule(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("定时任务：2秒后--->客户端"+channel.remoteAddress()+"发送消息:"+buf.toString(CharsetUtil.UTF_8));
//            }
//        }, 2, TimeUnit.SECONDS);
//
//        //费时任务直接扔到任务队列
//        ctx.channel().eventLoop().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                    System.out.println("任务队列：1秒后--->客户端"+channel.remoteAddress()+"发送消息:"+buf.toString(CharsetUtil.UTF_8));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });

    }

    //测试的时候没生效？不管了
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("trigger触发");
        if (evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            String eventType="";
            switch (idleStateEvent.state()){
                case ALL_IDLE:
                    eventType="读写空闲";
                    break;
                case READER_IDLE:
                    eventType="读空闲";
                    break;
                case WRITER_IDLE:
                    eventType="写空闲";
                    break;
            }
            System.out.println("当前的状态："+eventType);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        SocketAddress socketAddress = ctx.channel().remoteAddress();
        System.out.println(socketAddress+"离开了聊天");
        ctx.close();
    }
}
