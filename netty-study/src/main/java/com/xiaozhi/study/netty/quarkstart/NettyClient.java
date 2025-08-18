package com.xiaozhi.study.netty.quarkstart;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author DD
 */
public class NettyClient {

    @SneakyThrows
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel sc) {
                        sc.pipeline()
                                .addLast(new StringEncoder(StandardCharsets.UTF_8));
                    }
                });
        ChannelFuture cf = bootstrap.connect("127.0.0.1", 6666);
        // 处理回调监听
        cf.addListener(f -> {
            if (f.isSuccess()) {
                cf.channel().writeAndFlush("hello wold");
            }
        });
    }
}
