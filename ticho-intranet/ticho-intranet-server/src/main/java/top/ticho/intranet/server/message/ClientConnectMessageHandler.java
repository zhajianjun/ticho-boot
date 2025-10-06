package top.ticho.intranet.server.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.TiIntranetConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.entity.IntranetClient;
import top.ticho.intranet.server.support.IntranetClientSupport;

import java.util.Optional;

/**
 * 客户端连接消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public record ClientConnectMessageHandler(IntranetClientSupport intranetClientSupport) implements ClientMessageHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel clientChannel = ctx.channel();
        // log.warn("[6][服务端]接收客户端连接信息{}, 消息{}", channel, message);
        String requestId = message.requestId();
        String accessKey = new String(message.data());
        Optional<IntranetClient> clientInfoOpt = intranetClientSupport.findByAccessKey(accessKey);
        Optional<Channel> clientChannelOpt = clientInfoOpt.map(IntranetClient::getChannel);
        if (clientChannelOpt.isEmpty()) {
            log.warn("该秘钥没有可用通道{}", accessKey);
            clientChannel.close();
            return;
        }
        Channel clientChannelGet = clientChannelOpt.get();
        Channel requestChannel = intranetClientSupport.getRequestChannel(clientChannelGet, requestId);
        if (!IntranetUtil.isActive(requestChannel)) {
            return;
        }
        clientChannel.attr(TiIntranetConst.REQUEST_ID).set(requestId);
        clientChannel.attr(TiIntranetConst.ACCESS_KEY).set(accessKey);
        clientChannel.attr(TiIntranetConst.CHANNEL).set(requestChannel);
        requestChannel.attr(TiIntranetConst.CHANNEL).set(clientChannel);
        requestChannel.config().setOption(ChannelOption.AUTO_READ, true);
    }

}
