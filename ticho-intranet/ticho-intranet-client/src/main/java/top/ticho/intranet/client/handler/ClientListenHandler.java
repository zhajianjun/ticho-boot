package top.ticho.intranet.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.message.AbstractServerMessageHandler;
import top.ticho.intranet.client.message.ServerMessageCloseHandler;
import top.ticho.intranet.client.message.ServerMessageConnectHandler;
import top.ticho.intranet.client.message.ServerMessageDisconnectHandler;
import top.ticho.intranet.client.message.ServerMessageTransferHandler;
import top.ticho.intranet.client.message.ServerMessageUnknownHandler;
import top.ticho.intranet.client.repository.AppReposipory;
import top.ticho.intranet.client.repository.ServerRepository;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * 服务端监听处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientListenHandler extends SimpleChannelInboundHandler<Message> {

    private final ServerRepository serverRepository;
    private final AppReposipory appReposipory;
    public final Map<Byte, AbstractServerMessageHandler> MAP = new HashMap<>();
    public final AbstractServerMessageHandler UNKNOWN = new ServerMessageUnknownHandler();

    public ClientListenHandler(ClientContext clientContext) {
        this.serverRepository = clientContext.serverRepository();
        this.appReposipory = clientContext.appReposipory();
        ServerMessageConnectHandler clientConnectHandle = new ServerMessageConnectHandler();
        ServerMessageDisconnectHandler clientDisconnectHandle = new ServerMessageDisconnectHandler();
        ServerMessageTransferHandler clientTransferHandle = new ServerMessageTransferHandler();
        ServerMessageCloseHandler clientCloseHandle = new ServerMessageCloseHandler();
        // MAP.put(Message.AUTH, null);
        MAP.put(Message.DISABLED_ACCESS_KEY, clientCloseHandle);
        MAP.put(Message.CONNECT, clientConnectHandle);
        MAP.put(Message.DISCONNECT, clientDisconnectHandle);
        MAP.put(Message.TRANSFER, clientTransferHandle);
        MAP.values().forEach(item -> {
            item.setServerRepository(serverRepository);
            item.setAppReposipory(appReposipory);
            item.setClientProperty(clientContext.clientProperty());
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        byte type = msg.getType();
        AbstractServerMessageHandler clientHandle = MAP.getOrDefault(type, UNKNOWN);
        clientHandle.channelRead0(ctx, msg);
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
        if (serverRepository.getServerChannel() == clientChannel) {
            serverRepository.setServerChannel(null);
            appReposipory.clearRequestChannels();
            serverRepository.restart();
        } else {
            Channel requestCHannel = clientChannel.attr(CommConst.CHANNEL).get();
            IntranetUtil.close(requestCHannel);
        }
        serverRepository.removeReadyServerChannel(clientChannel);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端异常 {} {}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

}
