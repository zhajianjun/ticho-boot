package top.ticho.intranet.client.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.client.listener.AppConnectListener;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;

/**
 * 服务端通道连接消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public record ServerMessageConnectHandler(IntranetClientHandler intranetClientHandler) implements AbstractServerMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel serverChannel = ctx.channel();
        String requestId = message.requestId();
        String[] endpoint = new String(message.data()).split(CommConst.COLON);
        String host = endpoint[0];
        int port = Integer.parseInt(endpoint[1]);
        AppConnectListener listener = new AppConnectListener(intranetClientHandler, serverChannel, requestId);
        // log.debug("[客户端]连接{}:{}", host, port);
        // log.warn("[4][客户端]接收连接信息, 连接通道{}, 消息{}", serverChannel, message);
        intranetClientHandler.request(host, port, listener);
    }

}
