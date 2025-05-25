package top.ticho.intranet.client.listener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.repository.ClientRepository;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.prop.ClientProperty;

/**
 * 服务端权限校验监听器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerAuthCheckListener implements ChannelFutureListener {

    private final ClientRepository clientRepository;
    private final ClientProperty clientProperty;

    public ServerAuthCheckListener(ClientRepository clientRepository, ClientProperty clientProperty) {
        this.clientRepository = clientRepository;
        this.clientProperty = clientProperty;
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        String host = clientProperty.getServerHost();
        int port = clientProperty.getServerPort();
        // future.isSuccess() = false则表示连接服务端失败，尝试重连
        if (!future.isSuccess()) {
            log.warn("连接服务端[{}:{}]失败, error：{}", host, port, future.cause().getMessage());
            // 尝试重连
            clientRepository.restart();
            return;
        }
        // 连接成功处理
        Channel channel = future.channel();
        // 连接服务端的通道添加到 通道工厂中
        clientRepository.setServerChannel(channel);
        // 通道传输权限信息给服务端进行校验，由服务端校验是否关闭还是正常连接
        Message msg = new Message();
        msg.setType(Message.AUTH);
        msg.setUri(clientProperty.getAccessKey());
        channel.writeAndFlush(msg);
        // 重连后初始化sleepTime
        clientRepository.initSleepTime();
        // log.warn("[1]连接服务端成功：{}", channel);
        log.info("连接服务端{}]成功，校验权限中", channel);
    }

}
