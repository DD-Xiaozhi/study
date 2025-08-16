package com.xiaozhi.study.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author DD
 */
public class NIOSever {

    public static void main(String[] args) throws IOException {
        // 创建选择器
        Selector selector = Selector.open();
        // 创建 channel
        ServerSocketChannel ssc = ServerSocketChannel.open()
                .bind(new InetSocketAddress("127.0.0.1", 8080));
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 设置非阻塞模式
        ssc.configureBlocking(false);
        // 注册 channel 到 selector 中，监听接收事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    // 获取客户端 channel
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端已连接...");
                } else if (selectionKey.isReadable()) {
                    SocketChannel sc = (SocketChannel) selectionKey.channel();
                    int len;
                    while ((len = sc.read(byteBuffer)) > 0) {
                        // 切换成读模式
                        byteBuffer.flip();
                        String msg = new String(byteBuffer.array(), 0, len);
                        System.out.println("接收到客户端消息：" + msg);
                    }
                    // 清楚缓存
                    byteBuffer.clear();
                }
            }
        }
    }
}
