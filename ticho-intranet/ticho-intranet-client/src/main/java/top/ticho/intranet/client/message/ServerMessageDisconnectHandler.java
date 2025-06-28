package top.ticho.intranet.client.message;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;

/**
 * 服务端断开通道连接消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ServerMessageDisconnectHandler extends AbstractServerMessageHandler {

    public ServerMessageDisconnectHandler(IntranetClientHandler intranetClientHandler) {
        super(intranetClientHandler);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel serverChannel = ctx.channel();
        Channel requestCHannel = serverChannel.attr(CommConst.CHANNEL).get();
        if (null == requestCHannel) {
            return;
        }
        serverChannel.attr(CommConst.CHANNEL).set(null);
        intranetClientHandler.saveReadyServerChannel(serverChannel);
        requestCHannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

}
