package com.xiaozhi.study.netty.quarkstart;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author DD
 */
public class NettyServer {

    @SneakyThrows
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // boss event loop
        NioEventLoopGroup boss = new NioEventLoopGroup();
        // worker event loop
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            serverBootstrap.group(boss, worker)
                    // 指定服务类型通道为 NIO 类型
                    .channel(NioServerSocketChannel.class)
                    // 定义处理数据的管道
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {
                            // 定义处理数据的管道
                            ch.pipeline()
                                    // 字符串解码器
                                    .addLast(new StringDecoder(StandardCharsets.UTF_8))
                                    // 简单入栈处理器
                                    .addLast(new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                            System.out.println(STR."接收到客户端消息：\{msg}");
                                        }
                                    });
                        }
                    });
            // 同步阻塞等待连接
            ChannelFuture channelFuture = serverBootstrap.bind(6666).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 优雅关机
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
