package top.ticho.intranet.server.message;

import cn.hutool.core.util.StrUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.core.IntranetServerHandler;
import top.ticho.intranet.server.support.IntranetClientSupport;

/**
 * 客户端断开连接消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ClientDisconnectMessageHandler extends AbstractClientMessageHandler {
    private final IntranetClientSupport intranetClientSupport;

    public ClientDisconnectMessageHandler(IntranetServerHandler intranetServerHandler) {
        super(intranetServerHandler);
        this.intranetClientSupport = intranetServerHandler.intranetClientSupport();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();
        String requestId = message.requestId();
        String accessKey = channel.attr(CommConst.ACCESS_KEY).get();
        Channel requestChannel;
        if (StrUtil.isEmpty(accessKey)) {
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
        IntranetUtil.close(channel.attr(CommConst.CHANNEL).get());
        channel.attr(CommConst.REQUEST_ID).set(null);
        channel.attr(CommConst.ACCESS_KEY).set(null);
        channel.attr(CommConst.CHANNEL).set(null);
    }

}
