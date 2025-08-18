package com.xiaozhi.study.nio.quarkstart;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author DD
 */
public class NIOClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel sc = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
        sc.configureBlocking(false);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("我是客户端".getBytes(StandardCharsets.UTF_8));
        byteBuffer.flip();
        sc.write(byteBuffer);
        Thread.sleep(1000);
        byteBuffer.clear();
        sc.close();
    }
}
