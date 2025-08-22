package com.xiaozhi.study.ex;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 基于 netty & RESP协议 实现的 Redis 客户端
 * 客户端命令：
 *     set name ZhuZi
 * 转变为RESP指令：
 *     *3
 *     $3
 *     set
 *     $4
 *     name
 *     $5
 *     ZhuZi
 * RESP 指令以 * 开头，后面跟着参数数量。参数以 $ 开头，后面跟着参数长度，\n 结尾
 *
 * @author DD
 */
public class RedisClient {

    // 换行符的ASCII码
    static final byte[] LINE = { 13, 10 };

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println(Arrays.toString(LINE));
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                String cmdStr = "set name xiaozhi";
                                ByteBuf buffer = cmdToRESPStr(cmdStr);
                                ctx.writeAndFlush(buffer);
                            }

                            private ByteBuf cmdToRESPStr(String cmdStr) {
                                ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(1024);
                                String[] cmdList = cmdStr.split(StringUtils.SPACE);
                                buffer.writeBytes(("*" + cmdList.length).getBytes());
                                buffer.writeBytes(LINE);

                                for (String cmd : cmdList) {
                                    buffer.writeBytes(("$" + cmd.length()).getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes(cmd.getBytes());
                                    buffer.writeBytes(LINE);
                                }

                                return buffer;
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                System.out.println("redis 返回消息：" + msg);
                            }
                        });
                    }
                });
        bootstrap.connect("127.0.0.1", 6379).sync();
    }
}
