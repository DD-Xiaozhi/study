package com.xiaozhi.study.netty.decode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.StandardCharsets;

/**
 * LTC帧解码器
 * 通过头信息的方式进行解码，头信息记录数据的关键描述
 *
 * @author DD
 */
public class LengthFieldBasedFrameDecoderDemo {

    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG)
        );

        sendData(embeddedChannel, "欢迎光临");
        sendData(embeddedChannel, "hello 呀");
        sendData(embeddedChannel, "ABCDEFG");
    }

    private static void sendData(EmbeddedChannel embeddedChannel, String msg) {
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        // 获取字符串字节的长度
        int len = msgBytes.length;
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(1024);
        // 首位放长度
        buffer.writeInt(len)
                // 后面是内容
                .writeBytes(msgBytes);
        embeddedChannel.writeInbound(buffer);
    }


}
