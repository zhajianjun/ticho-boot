package top.ticho.intranet.client.core;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import top.ticho.intranet.client.repository.AppReposipory;
import top.ticho.intranet.client.repository.ClientRepository;
import top.ticho.intranet.common.prop.ClientProperty;

/**
 * 客户端处理器
 *
 * @author zhajianjun
 * @date 2025-05-20 22:48
 */
public record ClientHandler(
    NioEventLoopGroup workerGroup,
    ClientProperty clientProperty,
    ClientRepository clientRepository,
    AppReposipory appReposipory
) {
    public void start() {
        clientRepository.start();
    }

    public void restart() {
        clientRepository.restart();
    }

    public void stop(int status) {
        workerGroup.shutdownGracefully();
        appReposipory.clearRequestChannels();
        System.exit(status);
    }

    public void connectServer(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        clientRepository.connect(host, port, listener);
    }

    public Channel getServerChannel() {
        return clientRepository.getServerChannel();
    }

    public void saveServerChannel(Channel channel) {
        clientRepository.setServerChannel(channel);
    }

    public void removeServerChannel(Channel channel) {
        clientRepository.removeServerChannel(channel);
    }

    public void request(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        appReposipory.connect(host, port, listener);
    }

    public void saveRequestChannel(String requestId, Channel channel) {
        appReposipory.saveRequestChannel(requestId, channel);
    }

    public void clearRequestChannels() {
        appReposipory.clearRequestChannels();
    }

    public void removeRequestChannel(String requestId) {
        appReposipory.removeRequestChannel(requestId);
    }


}
