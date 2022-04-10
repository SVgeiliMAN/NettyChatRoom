package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class ChatRoomServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup=new NioEventLoopGroup();
        NioEventLoopGroup workGroup=new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {

            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("decoder",new StringDecoder())
                                    .addLast("encoder",new StringEncoder())
                                    .addLast(new IdleStateHandler(2,5,10, TimeUnit.SECONDS))
                                    .addLast(new NettyServerHandler());


                        }
                    });
            System.out.println("服务器启动中。。。。");
            ChannelFuture channelFuture = bootstrap.bind(1234).sync();
            //添加监听器回调。。。
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (channelFuture.isSuccess()){
                        System.out.println("服务器监听成功！");
                    }else{
                        System.out.println("服务器监听失败！");
                    }
                }
            });

            //阻塞
            channelFuture.channel().closeFuture().sync();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }


}
