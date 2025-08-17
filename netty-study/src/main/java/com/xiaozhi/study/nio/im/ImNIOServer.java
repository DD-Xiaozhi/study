package com.xiaozhi.study.nio.im;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 NIO 实现 IM
 *
 * @author DD
 */
public class ImNIOServer {

    public static void main(String[] args) throws IOException {
        Map<SocketChannel, String> sessionMap = new ConcurrentHashMap<>();
        ServerSocketChannel ssc = ServerSocketChannel.open().bind(new InetSocketAddress(9999));
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                try {
                    iterator.remove();
                    SocketChannel socketChannel = ssc.accept();
                    if (selectionKey.isAcceptable()) {
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        // 将连接加入到会话中
                        String sessionId = UUID.randomUUID().toString();
                        sessionMap.put(socketChannel, sessionId);
                    } else if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        int len;
                        if ((len = channel.read(byteBuffer)) != -1) {
                            byteBuffer.flip();
                            String msg = new String(byteBuffer.array(), 0, len);
                            for (SocketChannel sessionChannel : sessionMap.keySet()) {
                                if (sessionChannel != channel) {
                                    String consent = STR."\{sessionMap.get(sessionChannel)}: \{msg}";
                                    byteBuffer.clear();
                                    byteBuffer.put(consent.getBytes(StandardCharsets.UTF_8));
                                    byteBuffer.flip();
                                    sessionChannel.write(byteBuffer);
                                }
                            }
                        }
                        byteBuffer.clear();
                    }
                } catch (IOException e) {
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    System.out.println(STR."客户端连接异常或重置: [ ip: \{channel.getRemoteAddress()}, error msg: \{e.getMessage()} ]}");

                    // 关闭通道和监听
                    channel.close();
                    selectionKey.channel();
                    sessionMap.remove(channel);
                }
            }
        }
    }
}
