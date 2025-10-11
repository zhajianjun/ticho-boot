package top.ticho.intranet.server.listener;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.TiIntranetConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.core.IntranetServerHandler;
import top.ticho.intranet.server.message.ClientAuthMessageHandler;
import top.ticho.intranet.server.message.ClientConnectMessageHandler;
import top.ticho.intranet.server.message.ClientDisconnectMessageHandler;
import top.ticho.intranet.server.message.ClientHeartbeatMessageHandler;
import top.ticho.intranet.server.message.ClientMessageHandler;
import top.ticho.intranet.server.message.ClientMessageUnknownHandler;
import top.ticho.intranet.server.message.ClientTransferMessageHandler;
import top.ticho.intranet.server.support.IntranetClientSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 客户端消息监听器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientMessageListener extends SimpleChannelInboundHandler<Message> {
    private final IntranetClientSupport intranetClientSupport;
    private final Map<Byte, ClientMessageHandler> MAP;
    private final ClientMessageHandler UNKNOWN;

    public ClientMessageListener(IntranetServerHandler intranetServerHandler) {
        this.intranetClientSupport = intranetServerHandler.intranetClientSupport();
        this.MAP = new HashMap<>();
        this.UNKNOWN = new ClientMessageUnknownHandler();
        ClientAuthMessageHandler serverAuthHandle = new ClientAuthMessageHandler(intranetClientSupport, intranetServerHandler.serverStatus());
        ClientConnectMessageHandler serverConnectHandle = new ClientConnectMessageHandler(intranetClientSupport);
        ClientDisconnectMessageHandler serverDisconnectHandle = new ClientDisconnectMessageHandler(intranetClientSupport);
        ClientHeartbeatMessageHandler serverHeartbeatHandle = new ClientHeartbeatMessageHandler();
        ClientTransferMessageHandler serverTransferHandle = new ClientTransferMessageHandler();
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
        intranetClientSupport.closeRequestChannelByChannel(ctx.channel());
        log.error("客户端异常 {} {}", ctx.channel(), cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) {
        ClientMessageHandler serverHandle = MAP.getOrDefault(message.type(), UNKNOWN);
        serverHandle.channelRead0(ctx, message);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Channel extraChannel = channel.attr(TiIntranetConst.CHANNEL).get();
        if (null != extraChannel) {
            extraChannel.config().setOption(ChannelOption.AUTO_READ, channel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        Channel extraChannel = channel.attr(TiIntranetConst.CHANNEL).get();
        String accessKey = channel.attr(TiIntranetConst.ACCESS_KEY).get();
        if (IntranetUtil.isActive(extraChannel)) {
            String requestId = channel.attr(TiIntranetConst.REQUEST_ID).get();
            // 移除requestId的map信息
            intranetClientSupport.removeRequestChannel(accessKey, requestId);
            extraChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            extraChannel.close();
        } else {
            // 关闭客户端通道、请求通道
            intranetClientSupport.closeRequestChannelByAccessKey(accessKey);
            IntranetUtil.close(channel);
        }
        super.channelInactive(ctx);
    }

}
