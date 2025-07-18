package top.ticho.intranet.client.listener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.client.message.AbstractServerMessageHandler;
import top.ticho.intranet.client.message.ServerMessageAuthResponseHandler;
import top.ticho.intranet.client.message.ServerMessageCloseHandler;
import top.ticho.intranet.client.message.ServerMessageConnectHandler;
import top.ticho.intranet.client.message.ServerMessageDisconnectHandler;
import top.ticho.intranet.client.message.ServerMessageHeartbeatHandler;
import top.ticho.intranet.client.message.ServerMessageStartingHandler;
import top.ticho.intranet.client.message.ServerMessageTransferHandler;
import top.ticho.intranet.client.message.ServerMessageUnknownHandler;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * 服务端消息监听器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerMessageListener extends SimpleChannelInboundHandler<Message> {

    private final IntranetClientHandler intranetClientHandler;
    public final Map<Byte, AbstractServerMessageHandler> MAP;
    public final AbstractServerMessageHandler UNKNOWN;

    public ServerMessageListener(IntranetClientHandler intranetClientHandler) {
        this.intranetClientHandler = intranetClientHandler;
        this.UNKNOWN = new ServerMessageUnknownHandler(intranetClientHandler);
        this.MAP = new HashMap<>();
        ServerMessageConnectHandler clientConnectHandle = new ServerMessageConnectHandler(intranetClientHandler);
        ServerMessageDisconnectHandler clientDisconnectHandle = new ServerMessageDisconnectHandler(intranetClientHandler);
        ServerMessageTransferHandler clientTransferHandle = new ServerMessageTransferHandler(intranetClientHandler);
        ServerMessageCloseHandler clientCloseHandle = new ServerMessageCloseHandler(intranetClientHandler);
        ServerMessageHeartbeatHandler heartbeatHandler = new ServerMessageHeartbeatHandler(intranetClientHandler);
        ServerMessageAuthResponseHandler authResponseHandler = new ServerMessageAuthResponseHandler(intranetClientHandler);
        ServerMessageStartingHandler startingHandler = new ServerMessageStartingHandler(intranetClientHandler);
        // MAP.put(Message.AUTH, null);
        MAP.put(Message.DISABLED_ACCESS_KEY, clientCloseHandle);
        MAP.put(Message.AUTH, authResponseHandler);
        MAP.put(Message.CONNECT, clientConnectHandle);
        MAP.put(Message.DISCONNECT, clientDisconnectHandle);
        MAP.put(Message.TRANSFER, clientTransferHandle);
        MAP.put(Message.HEARTBEAT, heartbeatHandler);
        MAP.put(Message.STARTING, startingHandler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) {
        byte type = message.type();
        AbstractServerMessageHandler clientHandle = MAP.getOrDefault(type, UNKNOWN);
        clientHandle.channelRead0(ctx, message);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel clientChannel = ctx.channel();
        Channel requestCHannel = clientChannel.attr(CommConst.CHANNEL).get();
        if (null != requestCHannel) {
            requestCHannel.config().setOption(ChannelOption.AUTO_READ, clientChannel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel clientChannel = ctx.channel();
        if (intranetClientHandler.getServerChannel() == clientChannel) {
            intranetClientHandler.setServerChannel(null);
            intranetClientHandler.clearRequestChannels();
            intranetClientHandler.restart();
        } else {
            Channel requestCHannel = clientChannel.attr(CommConst.CHANNEL).get();
            IntranetUtil.close(requestCHannel);
        }
        intranetClientHandler.removeReadyServerChannel(clientChannel);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端异常 {} {}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

}
