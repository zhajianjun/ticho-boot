package top.ticho.intranet.client.listener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.support.ApplicationSupport;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;

/**
 * 客户端连接监听器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
@AllArgsConstructor
public class ServerConnectListener implements ChannelFutureListener {

    private final ApplicationSupport applicationSupport;
    private final String acccessKey;
    private final Channel serverChannel;
    private final Channel requestChannel;
    private final String requestId;

    @Override
    public void operationComplete(ChannelFuture future) {
        if (!future.isSuccess()) {
            Message message = new Message(Message.DISCONNECT, requestId, null);
            this.serverChannel.writeAndFlush(message);
            log.error("连接服务端失败:{}", future.cause().getMessage());
            return;
        }
        Channel clientChannel = future.channel();
        clientChannel.attr(CommConst.CHANNEL).set(this.requestChannel);
        this.requestChannel.attr(CommConst.CHANNEL).set(clientChannel);
        Message message = new Message(Message.CONNECT, requestId, acccessKey.getBytes());
        clientChannel.writeAndFlush(message);
        this.requestChannel.config().setOption(ChannelOption.AUTO_READ, true);
        applicationSupport.saveRequestChannel(requestId, requestChannel);
        this.requestChannel.attr(CommConst.REQUEST_ID).set(requestId);
        // log.warn("[5][客户端]连接信息回传服务端，回传通道{}，携带通道{}，消息{}", clientChannel, requestChannel, message);
    }

}
