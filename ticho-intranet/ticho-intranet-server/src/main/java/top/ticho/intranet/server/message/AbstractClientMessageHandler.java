package top.ticho.intranet.server.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.core.ServerHandler;


/**
 * 客户端消息处理器抽象类
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public abstract class AbstractClientMessageHandler {

    protected final ServerHandler serverHandler;

    public AbstractClientMessageHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    /**
     * 读取服务端信息进行不同的处理
     *
     * @param ctx     通道处理上线文
     * @param message 服务端传输的信息
     */
    public abstract void channelRead0(ChannelHandlerContext ctx, Message message);

    /**
     * 通知
     *
     * @param channel 通道
     * @param msgType msg类型
     * @param serial  序列号
     * @param data    传输数据
     */
    protected void notify(Channel channel, byte msgType, Long serial, byte[] data) {
        if (!IntranetUtil.isActive(channel)) {
            return;
        }
        Message message = new Message();
        if (null != serial) {
            message.setSerial(serial);
        }
        message.setType(msgType);
        message.setData(data);
        channel.writeAndFlush(message);
    }

}
