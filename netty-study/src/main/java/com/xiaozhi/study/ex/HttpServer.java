package com.xiaozhi.study.ex;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;

/**
 * http 服务器
 *
 * @author DD
 */
public class HttpServer {

    @SneakyThrows
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel sc) throws Exception {
                        ChannelPipeline pipeline = sc.pipeline();
                        // 添加一个Netty提供的HTTP处理器
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("消息类型：" + msg.getClass());
                                // 传递给下一个管道处理器处理，类似于 Filter 的 doFilter（责任链模式）
                                super.channelRead(ctx, msg);
                            }
                        });

                        pipeline.addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
                                System.out.println("客户端 url：" + request.getUri());

                                DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                                        request.getProtocolVersion(), HttpResponseStatus.OK);

                                String content = "<h1>hello world</h1>";
                                byte[] data = content.getBytes(StandardCharsets.UTF_8);

                                // 设置响应头
                                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, data.length);

                                // 设置响应体
                                response.content().writeBytes(data);

                                // 响应数据
                                ctx.writeAndFlush(response);
                            }
                        });
                    }
                });

        serverBootstrap.bind("127.0.0.1", 8001).sync();
    }
}
