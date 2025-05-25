package top.ticho.intranet.client.listener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.ClientHandler;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;

/**
 * App请求监听器
 *
 * @author zhajianjun
 * @date 2025-05-24 13:42
 */
@Slf4j
public class AppRequestListener extends SimpleChannelInboundHandler<ByteBuf> {

    private final ClientHandler clientHandler;

    public AppRequestListener(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        Channel responseChannel = ctx.channel();
        Channel serverChannel = responseChannel.attr(CommConst.CHANNEL).get();
        String uri = responseChannel.attr(CommConst.URI).get();
        if (serverChannel == null) {
            responseChannel.close();
            return;
        }
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        Message msg = new Message();
        msg.setType(Message.TRANSFER);
        msg.setUri(uri);
        msg.setData(data);
        serverChannel.writeAndFlush(msg);
        // log.warn("[9][客户端]响应信息回传，响应通道{}，回传通道{}，消息{}", responseChannel, serverChannel, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel requestCHannel = ctx.channel();
        String uri = requestCHannel.attr(CommConst.URI).get();
        clientHandler.removeRequestChannel(uri);
        Channel clientChannel = requestCHannel.attr(CommConst.CHANNEL).get();
        if (null != clientChannel) {
            Message msg = new Message();
            msg.setType(Message.DISCONNECT);
            msg.setUri(uri);
            clientChannel.writeAndFlush(msg);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        Channel serverChannel = requestChannel.attr(CommConst.CHANNEL).get();
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
