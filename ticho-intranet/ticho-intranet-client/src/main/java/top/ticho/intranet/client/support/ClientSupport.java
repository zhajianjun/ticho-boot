package top.ticho.intranet.client.support;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import top.ticho.intranet.client.listener.ServerAuthCheckListener;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.prop.ClientProperty;
import top.ticho.intranet.common.util.IntranetUtil;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 客户端
 *
 * @author zhajianjun
 * @date 2025-05-20 22:51
 */
public class ClientSupport {
    /** 等待间隔 */
    public static final long[] WAIT_INTERVALS = {
        5_000, 10_000, 30_000, 60_000, 120_000, 180_000, 600_000, 1_800_000, 1_800_000, 1_800_000, 3_600_000
    };
    /**
     * 就绪状态服务通道队列
     * 当和服务端交互不活跃的情况下会暂时把不活跃的通道放在队列里，重新交互时优先去队列的通道去进行交互，为空时讲重新连接服务端产生新通道进行交互
     */
    private final ConcurrentLinkedQueue<Channel> readyServerChannels;
    private final ClientProperty clientProperty;
    private volatile Channel serverChannel;
    private final Bootstrap clientBootstrap;
    private int retryIndex;

    public ClientSupport(ClientProperty clientProperty, Bootstrap clientBootstrap) {
        this.clientProperty = clientProperty;
        this.clientBootstrap = clientBootstrap;
        this.readyServerChannels = new ConcurrentLinkedQueue<>();
        initRetryIndex();
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
        clientBootstrap.connect(host, port).addListener(listener);
    }

    public void start() {
        String host = clientProperty.getServerHost();
        int port = clientProperty.getServerPort();
        // 连接远程服务器，并添加监听器发送accessKey验证权限，服务端验证失败会关闭连接
        connect(host, port, new ServerAuthCheckListener(this, clientProperty));
    }

    public void restart() {
        this.waitMoment();
        this.start();
    }

    public void waitMoment() {
        // 获取当前等待时间
        long waitTime = WAIT_INTERVALS[retryIndex];
        IntranetUtil.sleep(waitTime);
        // 更新索引，若已到最后一个则重置并初始化
        retryIndex = (retryIndex + 1) % WAIT_INTERVALS.length;
        if (this.retryIndex == 0) {
            initRetryIndex();
        }
    }

    public void initRetryIndex() {
        this.retryIndex = 0;
    }

}
