package top.ticho.intranet.server.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.TiIntranetConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;

/**
 * 客户端信息传输消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public record ClientTransferMessageHandler() implements ClientMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();
        Channel requestChannel = channel.attr(TiIntranetConst.CHANNEL).get();
        if (!IntranetUtil.isActive(requestChannel)) {
            log.warn("请求通道未活跃，无法转发消息");
            return;
        }
        ByteBuf data = ctx.alloc().buffer(message.data().length);
        try {
            data.writeBytes(message.data());
            // 添加监听器，确保写入失败时释放ByteBuf
            ChannelFuture future = requestChannel.writeAndFlush(data);
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.error("写入请求通道失败", channelFuture.cause());
                }
            });
        } catch (Exception e) {
            // 发生异常时手动释放ByteBuf
            log.error("转发消息时发生异常", e);
            data.release();
        }
    }

}
