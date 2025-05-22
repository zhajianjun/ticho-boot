package top.ticho.intranet.client.repository;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import top.ticho.intranet.client.handler.ServerAuthAfterHander;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.prop.ClientProperty;
import top.ticho.intranet.common.util.IntranetUtil;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author zhajianjun
 * @date 2025-05-20 22:51
 */
public class ClientRepository {
    /**
     * 就绪状态服务通道队列
     * 当和服务端交互不活跃的情况下会暂时把不活跃的通道放在队列里，重新交互时优先去队列的通道去进行交互，为空时讲重新连接服务端产生新通道进行交互
     */
    private final ConcurrentLinkedQueue<Channel> readyServerChannels;
    private final ClientProperty clientProperty;
    private volatile Channel serverChannel;
    private Bootstrap bootstrap;
    private long sleepTime;

    public ClientRepository(ClientProperty clientProperty) {
        this.clientProperty = clientProperty;
        this.readyServerChannels = new ConcurrentLinkedQueue<>();
        initSleepTime();
    }

    public void addBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public Channel getServerChannel() {
        return serverChannel;
    }

    public void setServerChannel(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    /**
     * 添加就绪状态的服务通道
     */
    public void saveReadyServerChannel(Channel channel) {
        if (readyServerChannels.size() > clientProperty.getMaxPoolSize()) {
            channel.close();
            return;
        }
        channel.config().setOption(ChannelOption.AUTO_READ, true);
        channel.attr(CommConst.CHANNEL).set(null);
        // 添加一个元素并返回true
        readyServerChannels.offer(channel);
    }

    /**
     * 获取就绪状态的服务通道
     */
    public Channel getReadyServerChannel() {
        return readyServerChannels.poll();
    }

    public void removeReadyServerChannel(Channel channel) {
        readyServerChannels.remove(channel);
    }

    public void connect(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        bootstrap.connect(host, port).addListener(listener);
    }

    public void start() {
        String host = clientProperty.getServerHost();
        int port = Optional.ofNullable(clientProperty.getServerPort()).orElse(CommConst.SERVER_PORT_DEFAULT);
        // 连接远程服务器，并添加监听器发送accessKey验证权限，服务端验证失败会关闭连接
        connect(host, port, new ServerAuthAfterHander(this, clientProperty));
    }

    public void restart() {
        this.waitMoment();
        this.start();
    }

    public void waitMoment() {
        // 超过一分钟则重置为一秒
        if (this.sleepTime > CommConst.ONE_MINUTE) {
            this.sleepTime = CommConst.ONE_SECOND;
        }
        // 时间翻倍
        this.sleepTime = this.sleepTime * 2;
        // 线程睡眠
        IntranetUtil.sleep(this.sleepTime);
    }

    public void initSleepTime() {
        this.sleepTime = CommConst.ONE_SECOND;
    }

}
