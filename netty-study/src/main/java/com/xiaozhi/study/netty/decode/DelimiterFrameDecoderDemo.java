package com.xiaozhi.study.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 分隔符解码器
 * 根据指定的分隔符结尾来区分包
 *
 * @author DD
 */
@Slf4j
public class DelimiterFrameDecoderDemo {

    public static void main(String[] args) {

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(1);
        buffer.writeByte(';');

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                // 指定结尾的分隔符，通过分隔符来区分数据结尾
                new DelimiterBasedFrameDecoder(1024, buffer),
                new LoggingHandler(LogLevel.DEBUG)
        );

        sendMsg(embeddedChannel, "hDDDJJ23432");
        sendMsg(embeddedChannel, "hello word");
        sendMsg(embeddedChannel, "world hello");
    }

    private static void sendMsg(EmbeddedChannel embeddedChannel, String msg) {
        msg += ";";
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(1024);
        buffer.writeBytes(msg.getBytes(StandardCharsets.UTF_8));

        embeddedChannel.writeInbound(buffer);
    }
}
