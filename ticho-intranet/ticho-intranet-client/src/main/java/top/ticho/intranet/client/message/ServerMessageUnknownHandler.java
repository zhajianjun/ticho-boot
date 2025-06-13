package top.ticho.intranet.client.message;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.ClientHandler;
import top.ticho.intranet.common.entity.Message;

/**
 * 服务端未知信息消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerMessageUnknownHandler extends AbstractServerMessageHandler {

    public ServerMessageUnknownHandler(ClientHandler clientHandler) {
        super(clientHandler);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        log.debug("接收到未知类型{}的消息，消息：{}，通道：{}", message.type(), new String(message.data()), ctx.channel());
    }

}
