package top.ticho.intranet.client.message;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;

import java.nio.charset.StandardCharsets;

/**
 * 服务端关闭消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerMessageCloseHandler extends AbstractServerMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Channel clientChannel = ctx.channel();
        log.info("服务端[{}]权限验证失败，{}", clientChannel.remoteAddress().toString(), StrUtil.str(msg.getData(), StandardCharsets.UTF_8));
        if (Boolean.TRUE.equals(clientProperty.getAutoClose())) {
            log.warn("客户端[{}]关闭连接", clientChannel.remoteAddress().toString());
            IntranetUtil.close(clientChannel);
            clientHander.stop(msg.getType());
        }
    }

}
