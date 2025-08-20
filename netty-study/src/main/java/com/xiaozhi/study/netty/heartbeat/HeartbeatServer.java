package com.xiaozhi.study.netty.heartbeat;

import com.xiaozhi.study.netty.heartbeat.handler.HeartbeatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author DD
 */
public class HeartbeatServer {

    @SneakyThrows
    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                // 开启长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) {
                        sc.pipeline()
                                    .addLast("IdleStateHandler", new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
                                    .addLast("Encode", new StringEncoder(StandardCharsets.UTF_8))
                                    .addLast("Decode", new StringDecoder(StandardCharsets.UTF_8))
                                    .addLast("HeartbeatHandler", new HeartbeatServerHandler())
                            ;
                    }
                });
        serverBootstrap.bind(6666);
    }
}
