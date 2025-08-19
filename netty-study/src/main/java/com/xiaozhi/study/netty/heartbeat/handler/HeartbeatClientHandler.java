package com.xiaozhi.study.netty.heartbeat.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 *
 * @author DD
 */
public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {

    // 通用心跳数据包
    private static final ByteBuf HEARTBEAT_DATA =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("I am Alive", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 判断当前出发的事件是不是闲置事件
        if (evt instanceof IdleStateEvent idleStateEvent) {
            // 判断当前事件是不是写闲置事件
            if (IdleState.WRITER_IDLE.equals(idleStateEvent.state())) {
                // 表示此时客户端有一段事件没有向服务端发送消息
                // 防止服务端断开连接，手动发送心跳包
                ctx.channel().writeAndFlush(HEARTBEAT_DATA);
                System.out.println("成功发送心跳包...");
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("与服务器进行连接...");
        ctx.channel().writeAndFlush("你好，我是客户端 DD");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("服务端关闭连接...");
    }

}
