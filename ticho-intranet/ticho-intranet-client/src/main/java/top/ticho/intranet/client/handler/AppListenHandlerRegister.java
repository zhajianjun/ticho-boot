package top.ticho.intranet.client.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.ticho.intranet.client.repository.AppReposipory;

/**
 * @author zhajianjun
 * @date 2025-05-19 23:13
 */
public class AppListenHandlerRegister extends ChannelInitializer<SocketChannel> {

    private final AppReposipory appReposipory;

    public AppListenHandlerRegister(AppReposipory appReposipory) {
        this.appReposipory = appReposipory;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(new AppListenHandler(appReposipory));
    }

}