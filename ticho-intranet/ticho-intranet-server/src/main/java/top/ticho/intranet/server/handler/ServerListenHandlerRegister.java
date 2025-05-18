package top.ticho.intranet.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.core.IdleChecker;
import top.ticho.intranet.common.core.MessageDecoder;
import top.ticho.intranet.common.core.MessageEncoder;
import top.ticho.intranet.server.repository.ClientRepository;

/**
 * @author zhajianjun
 * @date 2025-05-18 15:37
 */
@AllArgsConstructor
public class ServerListenHandlerRegister extends ChannelInitializer<SocketChannel> {

    private final ClientRepository clientRepository;

    public ServerListenHandlerRegister(ServerContext serverContext) {
        this.clientRepository = serverContext.clientRepository();
    }

    protected void initChannel(SocketChannel sc) {
        sc.pipeline().addLast(new MessageDecoder(CommConst.MAX_FRAME_LEN, CommConst.FIELD_OFFSET, CommConst.FIELD_LEN, CommConst.ADJUSTMENT, CommConst.INIT_BYTES_TO_STRIP));
        sc.pipeline().addLast(new MessageEncoder());
        sc.pipeline().addLast(new IdleChecker(CommConst.READ_IDLE_TIME, CommConst.WRITE_IDLE_TIME, 0));
        sc.pipeline().addLast(new ClientListenHandler(clientRepository));
    }

}
