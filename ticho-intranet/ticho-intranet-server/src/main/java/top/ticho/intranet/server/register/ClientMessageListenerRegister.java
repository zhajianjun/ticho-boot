package top.ticho.intranet.server.register;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.core.IdleChecker;
import top.ticho.intranet.common.core.MessageDecoder;
import top.ticho.intranet.common.core.MessageEncoder;
import top.ticho.intranet.server.core.ServerHandler;
import top.ticho.intranet.server.listener.ClientMessageListener;

/**
 * 客户端消息监听器注册器
 *
 * @author zhajianjun
 * @date 2025-05-18 15:37
 */
public class ClientMessageListenerRegister extends ChannelInitializer<SocketChannel> {
    private final ServerHandler serverHandler;

    public ClientMessageListenerRegister(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    protected void initChannel(SocketChannel sc) {
        sc.pipeline().addLast(new MessageDecoder(CommConst.MAX_FRAME_LEN, CommConst.FIELD_OFFSET, CommConst.FIELD_LEN, CommConst.ADJUSTMENT, CommConst.INIT_BYTES_TO_STRIP));
        sc.pipeline().addLast(new MessageEncoder());
        sc.pipeline().addLast(new IdleChecker(CommConst.READ_IDLE_TIME, CommConst.WRITE_IDLE_TIME, 0));
        sc.pipeline().addLast(new ClientMessageListener(serverHandler));
    }

}
