package top.ticho.intranet.server.message;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.server.core.IntranetServerHandler;

/**
 * 客户端未知消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientMessageUnknownHandler extends AbstractClientMessageHandler {

    public ClientMessageUnknownHandler(IntranetServerHandler intranetServerHandler) {
        super(intranetServerHandler);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        log.debug("接收到未知类型{}的消息,{}", message.type(), new String(message.data()));
    }

}
