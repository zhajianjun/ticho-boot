package top.ticho.intranet.client.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.ClientHandler;
import top.ticho.intranet.client.support.ClientSupport;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.prop.ClientProperty;

import java.nio.charset.StandardCharsets;

/**
 * 服务端启动中消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerMessageStartingHandler extends AbstractServerMessageHandler {
    private final ClientSupport clientSupport;
    private final ClientProperty clientProperty;

    public ServerMessageStartingHandler(ClientHandler clientHandler) {
        super(clientHandler);
        clientSupport = clientHandler.clientSupport();
        clientProperty = clientHandler.clientProperty();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        Channel clientChannel = ctx.channel();
        log.info("服务端[{}]正在启动中，消息：{}", clientChannel.remoteAddress(), new String(msg.getData(), StandardCharsets.UTF_8));
        // 权限校验
        Channel channel = ctx.channel();
        clientSupport.waitMoment();
        // 连接服务端的通道添加到 通道工厂中
        clientSupport.setServerChannel(channel);
        // 通道传输权限信息给服务端进行校验，由服务端校验是否关闭还是正常连接
        Message message = new Message();
        message.setType(Message.AUTH);
        message.setUri(clientProperty.getAccessKey());
        channel.writeAndFlush(message);
        log.info("重新校验服务端[{}]权限", channel);
    }

}
