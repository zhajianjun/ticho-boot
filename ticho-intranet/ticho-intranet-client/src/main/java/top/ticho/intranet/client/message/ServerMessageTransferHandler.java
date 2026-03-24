package top.ticho.intranet.client.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.TiIntranetConst;
import top.ticho.intranet.common.entity.Message;

/**
 * 服务端数据传输消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public record ServerMessageTransferHandler() implements ServerMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel serverChannel = ctx.channel();
        ByteBufAllocator alloc = ctx.alloc();
        Channel requestChannel = serverChannel.attr(TiIntranetConst.CHANNEL).get();
        if (requestChannel == null) {
            log.warn("请求通道为空，无法转发消息");
            return;
        }
        // 检查目标通道是否活跃
        if (!requestChannel.isActive()) {
            log.warn("请求通道未活跃，无法转发消息");
            return;
        }
        ByteBuf buf = alloc.buffer(message.data().length);
        try {
            buf.writeBytes(message.data());
            // 添加监听器，确保写入失败时释放ByteBuf
            ChannelFuture future = requestChannel.writeAndFlush(buf);
            future.addListener(channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.error("写入请求通道失败", channelFuture.cause());
                }
            });
            // log.warn("[8][客户端]接收到到客户端请求信息，接收通道{}，写入通道{}，消息{}", serverChannel, requestChannel, message);
        } catch (Exception e) {
            // 发生异常时手动释放ByteBuf
            log.error("转发消息时发生异常", e);
            buf.release();
        }
    }

}
