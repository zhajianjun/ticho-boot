package top.ticho.intranet.server.register;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.core.IdleChecker;
import top.ticho.intranet.common.core.MessageDecoder;
import top.ticho.intranet.common.core.MessageEncoder;
import top.ticho.intranet.server.core.ServerHandler;
import top.ticho.intranet.server.listener.ClientMessageListener;
import top.ticho.intranet.server.repository.ClientRepository;

/**
 * 客户端消息监听器注册器
 *
 * @author zhajianjun
 * @date 2025-05-18 15:37
 */
@AllArgsConstructor
public class ClientMessageListenerRegister extends ChannelInitializer<SocketChannel> {

    private final ClientRepository clientRepository;

    public ClientMessageListenerRegister(ServerHandler serverHandler) {
        this.clientRepository = serverHandler.clientRepository();
    }

    protected void initChannel(SocketChannel sc) {
        sc.pipeline().addLast(new MessageDecoder(CommConst.MAX_FRAME_LEN, CommConst.FIELD_OFFSET, CommConst.FIELD_LEN, CommConst.ADJUSTMENT, CommConst.INIT_BYTES_TO_STRIP));
        sc.pipeline().addLast(new MessageEncoder());
        sc.pipeline().addLast(new IdleChecker(CommConst.READ_IDLE_TIME, CommConst.WRITE_IDLE_TIME, 0));
        sc.pipeline().addLast(new ClientMessageListener(clientRepository));
    }

}
