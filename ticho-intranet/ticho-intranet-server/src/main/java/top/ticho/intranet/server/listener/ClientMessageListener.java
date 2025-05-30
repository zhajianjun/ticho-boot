package top.ticho.intranet.server.listener;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.core.ServerHandler;
import top.ticho.intranet.server.message.AbstractClientMessageHandler;
import top.ticho.intranet.server.message.ClientAuthMessageHandler;
import top.ticho.intranet.server.message.ClientConnectMessageHandler;
import top.ticho.intranet.server.message.ClientDisconnectMessageHandler;
import top.ticho.intranet.server.message.ClientHeartbeatMessageHandler;
import top.ticho.intranet.server.message.ClientMessageUnknownHandler;
import top.ticho.intranet.server.message.ClientTransferMessageHandler;
import top.ticho.intranet.server.support.ClientSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端消息监听器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientMessageListener extends SimpleChannelInboundHandler<Message> {
    private final ClientSupport clientSupport;
    public final Map<Byte, AbstractClientMessageHandler> MAP;
    public final AbstractClientMessageHandler UNKNOWN;

    public ClientMessageListener(ServerHandler serverHandler) {
        this.clientSupport = serverHandler.clientSupport();
        this.MAP = new HashMap<>();
        this.UNKNOWN = new ClientMessageUnknownHandler(serverHandler);
        ClientAuthMessageHandler serverAuthHandle = new ClientAuthMessageHandler(serverHandler);
        ClientConnectMessageHandler serverConnectHandle = new ClientConnectMessageHandler(serverHandler);
        ClientDisconnectMessageHandler serverDisconnectHandle = new ClientDisconnectMessageHandler(serverHandler);
        ClientHeartbeatMessageHandler serverHeartbeatHandle = new ClientHeartbeatMessageHandler(serverHandler);
        ClientTransferMessageHandler serverTransferHandle = new ClientTransferMessageHandler(serverHandler);
        // MAP.put(MsgType.AUTH, null);
        this.MAP.put(Message.AUTH, serverAuthHandle);
        this.MAP.put(Message.CONNECT, serverConnectHandle);
        this.MAP.put(Message.DISCONNECT, serverDisconnectHandle);
        this.MAP.put(Message.TRANSFER, serverTransferHandle);
        this.MAP.put(Message.HEARTBEAT, serverHeartbeatHandle);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 客户端异常时，把通道置为空
        clientSupport.closeRequestChannelByChannel(ctx.channel());
        log.error("客户端异常 {} {}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) {
        AbstractClientMessageHandler serverHandle = MAP.getOrDefault(message.type(), UNKNOWN);
        serverHandle.channelRead0(ctx, message);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Channel extraChannel = channel.attr(CommConst.CHANNEL).get();
        if (null != extraChannel) {
            extraChannel.config().setOption(ChannelOption.AUTO_READ, channel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Channel extraChannel = channel.attr(CommConst.CHANNEL).get();
        String accessKey = channel.attr(CommConst.ACCESS_KEY).get();
        if (IntranetUtil.isActive(extraChannel)) {
            String requestId = channel.attr(CommConst.REQUEST_ID).get();
            // 移除requestId的map信息
            clientSupport.removeRequestChannel(accessKey, requestId);
            extraChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            extraChannel.close();
        } else {
            // 关闭客户端通道、请求通道
            clientSupport.closeRequestChannelByAccessKey(accessKey);
            IntranetUtil.close(channel);
        }
        super.channelInactive(ctx);
    }

}
