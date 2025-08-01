package top.ticho.intranet.common.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.entity.Message;


/**
 * 心跳检测处理
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class IdleChecker extends IdleStateHandler {

    public IdleChecker(int readerIdleTime, int writerIdleTime, int allIdleTime) {
        super(readerIdleTime, writerIdleTime, allIdleTime);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        // 检测到通道空闲事件时执行以下代码
        if (IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT == evt) {
            Channel channel = ctx.channel();
            // 创建一个心跳消息对象
            Message message = new Message(Message.HEARTBEAT, null, "写空闲超时".getBytes());
            // 向通道写入心跳消息并刷新
            channel.writeAndFlush(message);
            log.debug("写空闲超时，心跳检测，通道：{}", channel.remoteAddress());
        } else if (IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT == evt) {
            // 如果是读取超时，则关闭通道
            ctx.channel().close();
        }
        // 调用父类方法处理通道空闲事件
        super.channelIdle(ctx, evt);
    }
}
