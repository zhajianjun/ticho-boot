package top.ticho.intranet.server.message;

import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.core.ServerHandler;
import top.ticho.intranet.server.entity.ClientInfo;
import top.ticho.intranet.server.repository.ClientRepository;

import java.util.Optional;

/**
 * 客户端连接消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientConnectMessageHandler extends AbstractClientMessageHandler {
    private final ClientRepository clientRepository;

    public ClientConnectMessageHandler(ServerHandler serverHandler) {
        super(serverHandler);
        this.clientRepository = serverHandler.clientRepository();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel clientChannel = ctx.channel();
        // log.warn("[6][服务端]接收客户端连接信息{}, 消息{}", channel, msg);
        String uri = message.getUri();
        if (StrUtil.isBlank(uri)) {
            log.warn("链接地址为空");
            clientChannel.close();
            return;
        }
        String[] tokens = uri.split("@");
        if (tokens.length != 2) {
            log.warn("链接地址不合法");
            clientChannel.close();
            return;
        }
        String requestId = tokens[0];
        String accessKey = tokens[1];
        Optional<ClientInfo> clientInfoOpt = clientRepository.findByAccessKey(accessKey);
        Optional<Channel> clientChannelOpt = clientInfoOpt.map(ClientInfo::getChannel);
        if (clientChannelOpt.isEmpty()) {
            log.warn("该秘钥没有可用通道{}", accessKey);
            clientChannel.close();
            return;
        }
        Channel clientChannelGet = clientChannelOpt.get();
        Channel requestChannel = clientRepository.getRequestChannel(clientChannelGet, requestId);
        if (!IntranetUtil.isActive(requestChannel)) {
            return;
        }
        clientChannel.attr(CommConst.URI).set(requestId);
        clientChannel.attr(CommConst.KEY).set(accessKey);
        clientChannel.attr(CommConst.CHANNEL).set(requestChannel);
        requestChannel.attr(CommConst.CHANNEL).set(clientChannel);
        requestChannel.config().setOption(ChannelOption.AUTO_READ, true);
    }

}
