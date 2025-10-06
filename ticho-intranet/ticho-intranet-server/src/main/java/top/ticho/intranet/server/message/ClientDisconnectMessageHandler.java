package top.ticho.intranet.server.message;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import top.ticho.intranet.common.constant.TiIntranetConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.support.IntranetClientSupport;
import top.ticho.tool.core.TiStrUtil;

/**
 * 客户端断开连接消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public record ClientDisconnectMessageHandler(IntranetClientSupport intranetClientSupport) implements ClientMessageHandler {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();
        String requestId = message.requestId();
        String accessKey = channel.attr(TiIntranetConst.ACCESS_KEY).get();
        Channel requestChannel;
        if (TiStrUtil.isBlank(accessKey)) {
            requestChannel = intranetClientSupport.removeRequestChannel(channel, requestId);
            if (IntranetUtil.isActive(requestChannel)) {
                requestChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
            return;
        }
        requestChannel = intranetClientSupport.removeRequestChannel(accessKey, requestId);
        if (!IntranetUtil.isActive(requestChannel)) {
            return;
        }
        requestChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        IntranetUtil.close(channel.attr(TiIntranetConst.CHANNEL).get());
        channel.attr(TiIntranetConst.REQUEST_ID).set(null);
        channel.attr(TiIntranetConst.ACCESS_KEY).set(null);
        channel.attr(TiIntranetConst.CHANNEL).set(null);
    }

}
