package top.ticho.intranet.server.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.server.core.ServerHandler;

import java.nio.charset.StandardCharsets;

/**
 * 客户端心跳消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientHeartbeatMessageHandler extends AbstractClientMessageHandler {

    public ClientHeartbeatMessageHandler(ServerHandler serverHandler) {
        super(serverHandler);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();
        log.debug("接收到客户端心跳检测，消息：{}，通道：{}", new String(message.getData(), StandardCharsets.UTF_8), channel);
        notify(channel, Message.HEARTBEAT, "SERVER CALLBACK".getBytes(StandardCharsets.UTF_8));
    }

}
