package top.ticho.intranet.client.message;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.entity.Message;

/**
 * 服务端关闭消息处理器
 *
 * @author zhajianjun
 * @date 2025-05-24 16:43
 */
@Slf4j
public record ServerMessageHeartbeatHandler() implements ServerMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        log.debug("接收到服务端心跳检测回传，消息：{}，通道：{}", new String(message.data()), ctx.channel());
    }

}
