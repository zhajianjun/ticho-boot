package top.ticho.intranet.client.listener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.ClientHandler;
import top.ticho.intranet.client.support.ApplicationSupport;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.prop.ClientProperty;


/**
 * app连接时处理
 * 1.激活连接
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
@AllArgsConstructor
public class AppConnectListener implements ChannelFutureListener {

    private final ClientHandler clientHandler;
    private final Channel serverChannel;
    private final String requestId;

    @Override
    public void operationComplete(ChannelFuture channelFuture) {
        if (!channelFuture.isSuccess()) {
            Message message = new Message(Message.DISCONNECT, requestId, null);
            this.serverChannel.writeAndFlush(message);
            return;
        }
        ClientProperty clientProperty = clientHandler.clientProperty();
        ApplicationSupport applicationSupport = clientHandler.applicationSupport();
        String accessKey = clientProperty.getAccessKey();
        // 访问的客户端通道
        Channel requestChannel = channelFuture.channel();
        requestChannel.config().setOption(ChannelOption.AUTO_READ, false);
        Channel readyServerChannel = clientHandler.getReadyServerChannel();
        if (readyServerChannel == null) {
            String host = clientProperty.getServerHost();
            int port = clientProperty.getServerPort();
            ServerConnectListener listener = new ServerConnectListener(applicationSupport, accessKey, serverChannel, requestChannel, requestId);
            clientHandler.connectServer(host, port, listener);
            return;
        }
        readyServerChannel.attr(CommConst.CHANNEL).set(requestChannel);
        requestChannel.attr(CommConst.CHANNEL).set(readyServerChannel);
        Message message = new Message(Message.CONNECT, requestId, accessKey.getBytes());
        readyServerChannel.writeAndFlush(message);
        requestChannel.config().setOption(ChannelOption.AUTO_READ, true);
        clientHandler.saveRequestChannel(requestId, requestChannel);
        requestChannel.attr(CommConst.REQUEST_ID).set(requestId);
        // log.warn("[5][客户端]连接信息回传服务端，回传通道{}，携带通道{}，消息{}", readyServerChannel, requestChannel, message);
    }

}
