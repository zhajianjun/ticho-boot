package top.ticho.intranet.server.register;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.ticho.intranet.common.constant.TiIntranetConst;
import top.ticho.intranet.common.core.IdleChecker;
import top.ticho.intranet.common.core.MessageDecoder;
import top.ticho.intranet.common.core.MessageEncoder;
import top.ticho.intranet.server.core.IntranetServerHandler;
import top.ticho.intranet.server.listener.ClientMessageListener;

/**
 * 客户端消息监听器注册器
 *
 * @author zhajianjun
 * @date 2025-05-18 15:37
 */
public class ClientMessageListenerRegister extends ChannelInitializer<SocketChannel> {
    private final IntranetServerHandler intranetServerHandler;

    public ClientMessageListenerRegister(IntranetServerHandler intranetServerHandler) {
        this.intranetServerHandler = intranetServerHandler;
    }

    protected void initChannel(SocketChannel sc) {
        sc.pipeline().addLast(new MessageDecoder(TiIntranetConst.MAX_FRAME_LEN, TiIntranetConst.FIELD_OFFSET, TiIntranetConst.FIELD_LEN, TiIntranetConst.ADJUSTMENT, TiIntranetConst.INIT_BYTES_TO_STRIP));
        sc.pipeline().addLast(new MessageEncoder());
        sc.pipeline().addLast(new IdleChecker(TiIntranetConst.READ_IDLE_TIME, TiIntranetConst.WRITE_IDLE_TIME, 0));
        sc.pipeline().addLast(new ClientMessageListener(intranetServerHandler));
    }

}
