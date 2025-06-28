package top.ticho.intranet.client.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.client.support.ClientSupport;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.prop.ClientProperty;

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

    public ServerMessageStartingHandler(IntranetClientHandler intranetClientHandler) {
        super(intranetClientHandler);
        clientSupport = intranetClientHandler.clientSupport();
        clientProperty = intranetClientHandler.clientProperty();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel serverChannel = ctx.channel();
        log.info("服务端[{}]正在启动中，消息：{}", serverChannel.remoteAddress(), new String(message.data()));
        // 权限校验
        clientSupport.waitMoment();
        // 连接服务端的通道添加到 通道工厂中
        clientSupport.setServerChannel(serverChannel);
        // 通道传输权限信息给服务端进行校验，由服务端校验是否关闭还是正常连接
        Message newMessage = new Message(Message.AUTH, null, clientProperty.getAccessKey().getBytes());
        serverChannel.writeAndFlush(newMessage);
        log.info("重新校验服务端[{}]权限", serverChannel);
    }

}
