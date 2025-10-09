package top.ticho.intranet.client.listener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.common.constant.TiIntranetConst;
import top.ticho.intranet.common.entity.Message;

/**
 * App请求监听器
 *
 * @author zhajianjun
 * @date 2025-05-24 13:42
 */
@Slf4j
public class AppRequestListener extends SimpleChannelInboundHandler<ByteBuf> {

    private final IntranetClientHandler intranetClientHandler;

    public AppRequestListener(IntranetClientHandler intranetClientHandler) {
        this.intranetClientHandler = intranetClientHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        Channel responseChannel = ctx.channel();
        Channel serverChannel = responseChannel.attr(TiIntranetConst.CHANNEL).get();
        String requestId = responseChannel.attr(TiIntranetConst.REQUEST_ID).get();
        if (serverChannel == null) {
            responseChannel.close();
            return;
        }
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        Message message = new Message(Message.TRANSFER, requestId, data);
        serverChannel.writeAndFlush(message);
        // log.warn("[9][客户端]响应信息回传，响应通道{}，回传通道{}，消息{}", responseChannel, serverChannel, message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel requestCHannel = ctx.channel();
        String requestId = requestCHannel.attr(TiIntranetConst.REQUEST_ID).get();
        intranetClientHandler.removeRequestChannel(requestId);
        Channel clientChannel = requestCHannel.attr(TiIntranetConst.CHANNEL).get();
        if (null != clientChannel) {
            Message message = new Message(Message.DISCONNECT, requestId, null);
            clientChannel.writeAndFlush(message);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        Channel serverChannel = requestChannel.attr(TiIntranetConst.CHANNEL).get();
        if (null != serverChannel) {
            serverChannel.config().setOption(ChannelOption.AUTO_READ, requestChannel.isWritable());
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("客户端异常 {} {}", ctx.channel(), cause.getMessage(), cause);
        super.exceptionCaught(ctx, cause);
    }

}
