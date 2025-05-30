package top.ticho.intranet.client.message;

import io.netty.channel.ChannelHandlerContext;
import top.ticho.intranet.client.core.ClientHandler;
import top.ticho.intranet.common.entity.Message;


/**
 * 服务端消息处理器抽象类
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public abstract class AbstractServerMessageHandler {

    protected final ClientHandler clientHandler;

    protected AbstractServerMessageHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * 读取服务端信息进行不同的处理
     *
     * @param ctx 通道处理上线文
     * @param message 服务端传输的信息
     */
    public abstract void channelRead0(ChannelHandlerContext ctx, Message message);

}
