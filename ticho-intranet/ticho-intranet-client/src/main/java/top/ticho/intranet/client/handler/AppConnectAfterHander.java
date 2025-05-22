package top.ticho.intranet.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.repository.AppReposipory;
import top.ticho.intranet.client.repository.ClientRepository;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.prop.ClientProperty;

import java.util.Optional;


/**
 * app连接后处理
 * 1.激活连接
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
@AllArgsConstructor
public class AppConnectAfterHander implements ChannelFutureListener {

    private final ClientRepository clientRepository;

    private final AppReposipory appReposipory;

    private final ClientProperty clientProperty;

    private final Channel serverChannel;

    private final String requestId;

    @Override
    public void operationComplete(ChannelFuture channelFuture) {
        if (!channelFuture.isSuccess()) {
            Message msg = new Message();
            msg.setType(Message.DISCONNECT);
            msg.setUri(requestId);
            this.serverChannel.writeAndFlush(msg);
            return;
        }
        // 访问的客户端通道
        Channel requestChannel = channelFuture.channel();
        requestChannel.config().setOption(ChannelOption.AUTO_READ, false);
        Channel readyServerChannel = clientRepository.getReadyServerChannel();
        if (readyServerChannel == null) {
            String host = clientProperty.getServerHost();
            int port = Optional.ofNullable(clientProperty.getServerPort()).orElse(CommConst.SERVER_PORT_DEFAULT);
            ServerConnectAfterHander listener = new ServerConnectAfterHander(appReposipory, clientProperty, serverChannel, requestChannel, requestId);
            clientRepository.connect(host, port, listener);
            return;
        }

        readyServerChannel.attr(CommConst.CHANNEL).set(requestChannel);
        requestChannel.attr(CommConst.CHANNEL).set(readyServerChannel);
        Message msg = new Message();
        msg.setType(Message.CONNECT);
        // requestId @ accessKey
        msg.setUri(requestId + "@" + clientProperty.getAccessKey());
        readyServerChannel.writeAndFlush(msg);
        requestChannel.config().setOption(ChannelOption.AUTO_READ, true);
        appReposipory.saveRequestChannel(requestId, requestChannel);
        requestChannel.attr(CommConst.URI).set(requestId);
        // log.warn("[5][客户端]连接信息回传服务端，回传通道{}，携带通道{}，消息{}", readyServerChannel, requestChannel, msg);
    }

}
