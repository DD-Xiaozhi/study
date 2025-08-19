package com.xiaozhi.study.netty.heartbeat;

import com.xiaozhi.study.netty.heartbeat.handler.HeartbeatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author DD
 */
public class HeartbeatClient {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try (EventLoopGroup eventLoopGroup = new NioEventLoopGroup()) {
            bootstrap.group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast("IdleStateHandler", new IdleStateHandler(0, 3, 0, TimeUnit.SECONDS))
                                    .addLast("Encode", new StringEncoder(StandardCharsets.UTF_8))
                                    .addLast("Decode", new StringDecoder(StandardCharsets.UTF_8))
                                    .addLast("HeartbeatHandler", new HeartbeatClientHandler())
                            ;
                        }
                    });
        }
    }
}
