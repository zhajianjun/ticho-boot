package top.ticho.intranet.client.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.ClientHandler;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;

/**
 * 服务端关闭消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerMessageCloseHandler extends AbstractServerMessageHandler {

    public ServerMessageCloseHandler(ClientHandler clientHandler) {
        super(clientHandler);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel serverChannel = ctx.channel();
        log.info("服务端{}权限验证失败，消息：{}", serverChannel.remoteAddress(), new String(message.data()));
        IntranetUtil.close(serverChannel);
        clientHandler.stop(message.type());
    }

}
