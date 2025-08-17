package com.xiaozhi.study.nio.im;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author DD
 */
public class IMNIOClient {

    @SneakyThrows
    public static void main(String[] args) {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(9999));
        sc.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_READ);
        Scanner scanner = new Scanner(System.in);

        // 处理服务器响应消息
        Thread.startVirtualThread(() -> {
            while (true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        if (selectionKey.isAcceptable()) {
                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ);
                        } else if (selectionKey.isReadable()) {
                            int len;
                            if ((len = sc.read(buffer)) != -1) {
                                buffer.flip();
                                String msg = new String(buffer.array(), 0, len, StandardCharsets.UTF_8);
                                System.out.println(STR."\r\{msg}");
                                System.out.print("请输入你要发送的消息：");
                            }
                        }
                        buffer.clear();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 处理用户信息读取发送服务
        System.out.print("请输入你要发送的消息：");
        while (true) {
            String msg = scanner.nextLine();
            if (StringUtils.isNotBlank(msg)) {
                buffer.put(msg.getBytes(StandardCharsets.UTF_8));
                try {
                    buffer.flip();
                    sc.write(buffer);
                    System.out.print(STR."\r我：\{msg}\n");
                    buffer.clear();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
