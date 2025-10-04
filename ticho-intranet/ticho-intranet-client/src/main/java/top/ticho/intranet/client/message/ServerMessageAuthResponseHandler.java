package top.ticho.intranet.client.message;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.common.entity.Message;

/**
 * 服务端关闭消息处理器
 *
 * @author zhajianjun
 * @date 2025-05-24 16:43
 */
@Slf4j
public record ServerMessageAuthResponseHandler(IntranetClientHandler intranetClientHandler) implements ServerMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        log.info("服务端{}权限校验成功，消息：{}", ctx.channel(), new String(message.data()));
    }

}
