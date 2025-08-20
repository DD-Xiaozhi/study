package com.xiaozhi.study.netty.heartbeat.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * 带有心跳机制的服务端实现
 *
 * @author DD
 */
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

    private static final ByteBuf HEARTBEAT_DATA =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("I am Alive", CharsetUtil.UTF_8));

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleState idleState = ((IdleStateEvent) evt).state();
        if (IdleState.READER_IDLE == idleState) {
            ctx.channel().close();
            System.out.println("未发送心跳包，关闭连接");
        } else {
            super.userEventTriggered(ctx, evt);
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ("I am Alive".equals(msg.toString())) {
            ctx.writeAndFlush("I know");
        }
        System.out.println(STR."收到客户端消息：\{msg}");
        super.channelRead(ctx, msg);
    }
}
