package com.xiaozhi.study.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;

/**
 * 定长帧解码器
 *
 * @author DD
 */
public class FixedLengthFrameDecoderDemo {

    // 单条消息长度
    private static final int msgLen = 8;

    public static void main(String[] args) {
        // EmbeddedChannel - Netty 提供的测试通道
        // 构造器参数是 ChannelHandler 数组，类似于 pipeline
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(msgLen),
                new LoggingHandler(LogLevel.DEBUG)
        );

        sendMsg(embeddedChannel, "hello");
        sendMsg(embeddedChannel, "hello123456");
        sendMsg(embeddedChannel, "hello123");
    }

    private static void sendMsg(EmbeddedChannel embeddedChannel, String msg) {
        if (msg.length() < msgLen) {
            for (int i = msg.length(); i < msgLen; i++) {
                msg += "*";
            }
        }

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(8);
        buffer.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
        embeddedChannel.writeOneInbound(buffer);
    }
}
