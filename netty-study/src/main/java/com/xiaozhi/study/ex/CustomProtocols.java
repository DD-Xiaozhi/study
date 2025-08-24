package com.xiaozhi.study.ex;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.*;
import java.util.List;

/**
 * 自定义协议，核心部分如下：
 *  - 协议头（Protocol Header）
 *      - 魔数（Magic Number）: 一个固定的、唯一的字节序列，用于快速识别数据包是否为你的协议。这能有效防止数据错乱或错误地处理其他协议的数据。
 *      - 版本号（Version）: 随着协议的发展，版本号可以确保新旧版本客户端的兼容性。
 *      - 消息类型（Message Type）: 区分数据包的类型，例如：心跳包、登录请求、数据传输等。这能让接收方迅速采取不同的处理逻辑。
 *      - 消息体长度（Body Length）: 指明消息体（实际数据）的长度。这是最关键的字段之一，能解决 TCP 粘包/拆包问题。
 *          接收方通过读取这个长度，就知道何时一个完整的数据包已接收完毕。
 *      - 序列号（Sequence Number）: 用于消息的顺序控制和去重。对于一些需要保证顺序的应用场景（如消息队列），这是必不可少的。
 *      - 正文长度：提供给 TCL 解码器使用
 *      - 序列化类型：正文选择的序列化方式，市面上面比较常见的是 Json、Protobuf 等序列化方式
 * - 消息体（Message Body）：消息体是协议中承载实际业务数据的地方。它的格式应根据你的业务需求来定。
 *      常见的编码格式：
 *          - JSON/XML: 易于阅读和调试，但解析开销较大，且占用空间多。适合对性能要求不高的场景。
 *          - Protobuf/Thrift: 谷歌和 Facebook 开发的跨语言序列化协议。
 *              它们是二进制格式，高效、体积小，但可读性差，需要定义 .proto 或 .thrift 文件。
 *              这是高性能服务间通信的首选。
 *          - 自定义二进制格式: 最灵活也最难维护的方式。可以根据业务场景精确控制每个字段的字节数，以实现最高性能，但需要严格的文档和工具支持。
 *
 * @author DD
 */
@ChannelHandler.Sharable
public class CustomProtocols extends MessageToMessageCodec<ByteBuf, CustomProtocols.Message> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          Message message, List<Object> list) throws Exception {
        // 创建消息缓冲池
        ByteBuf msgBuf = channelHandlerContext.alloc().buffer();
        msgBuf.writeBytes(message.getMagicNumber());
        msgBuf.writeFloat(message.getVersion());
        msgBuf.writeInt(message.getMessageType().getCode());
        msgBuf.writeInt(message.getSerializationType().getCode());

        // 这里可以做成策略模式，这里不展开
        if (SerializationType.JDK.equals(message.getSerializationType())) {
            // 创建字节数组输出流
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // 创建对象输出流，它会对java对象进行实例化操作
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(message.getMessageBody());
            byte[] byteArray = bos.toByteArray();

            msgBuf.writeInt(byteArray.length);
            msgBuf.writeBytes(byteArray);
        }

        // 传递给下一个管道处理器处理
        list.add(msgBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] magicNumber = byteBuf.readBytes(4).array();
        float version = byteBuf.readFloat();
        int messageType = byteBuf.readInt();
        int serializationType = byteBuf.readInt();
        int bodyLength = byteBuf.readInt();
        byte[] messageBody = byteBuf.readBytes(bodyLength).array();
        Object bodyObject = null;
        if (SerializationType.JDK.getCode().equals(serializationType)) {
            ByteArrayInputStream bis = new ByteArrayInputStream(messageBody);
            ObjectInputStream oos = new ObjectInputStream(bis);
            bodyObject = oos.readObject();
        }

        list.add(bodyObject);
    }

    @Data
    public static class Message {

        /**
         * 魔数 (4个字节)
         */
        private byte[] magicNumber = { 13, 14, 66, 66 };

        /**
         * 版本号
         */
        private float version = 0.1f;

        /**
         * 消息类型
         */
        private MessageType messageType;

        /**
         * 消息体长度
         */
        private int bodyLength;

        /**
         * 序列化方式
         */
        private SerializationType serializationType;

        /**
         * 消息体
         */
        private Object messageBody;
    }

     @Getter
    @AllArgsConstructor
    private enum SerializationType {

        JSON(0, "JSON"),
        STRING(1, "String"),
        PROTOBUF(2, "PROTOBUF"),
        JDK(3, "JDK");

        private final Integer code;
        private final String type;
    }

    @Getter
    @AllArgsConstructor
    private enum MessageType {

        HEARTBEAT(0, "HEARTBEAT"),
        DATA_TRANSFER(1, "DATA_TRANSFER");

        private final Integer code;
        private final String type;
    }
}
