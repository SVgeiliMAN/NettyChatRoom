package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

//SimpleChannelInboundHandler extends ChannelInboundHandlerAdapter 功能上做了增强，感觉没啥区别
public class SimpleNettyServerHandler extends SimpleChannelInboundHandler {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }



}
