package top.ticho.intranet.server.message;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.core.ServerHandler;
import top.ticho.intranet.server.support.ClientSupport;

/**
 * 客户端断开连接消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ClientDisconnectMessageHandler extends AbstractClientMessageHandler {
    private final ClientSupport clientSupport;

    public ClientDisconnectMessageHandler(ServerHandler serverHandler) {
        super(serverHandler);
        this.clientSupport = serverHandler.clientSupport();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();
        String requestId = message.getUri();
        String accessKey = channel.attr(CommConst.KEY).get();
        Channel requestChannel;
        if (StrUtil.isEmpty(accessKey)) {
            requestChannel = clientSupport.removeRequestChannel(channel, requestId);
            if (IntranetUtil.isActive(requestChannel)) {
                requestChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
            return;
        }
        requestChannel = clientSupport.removeRequestChannel(accessKey, requestId);
        if (!IntranetUtil.isActive(requestChannel)) {
            return;
        }
        requestChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        IntranetUtil.close(channel.attr(CommConst.CHANNEL).get());
        channel.attr(CommConst.URI).set(null);
        channel.attr(CommConst.KEY).set(null);
        channel.attr(CommConst.CHANNEL).set(null);
    }

}
