package top.ticho.intranet.server.message;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.common.ServerStatus;
import top.ticho.intranet.server.core.IntranetServerHandler;
import top.ticho.intranet.server.entity.IntranetClient;
import top.ticho.intranet.server.entity.IntranetPort;
import top.ticho.intranet.server.support.IntranetClientSupport;
import top.ticho.tool.core.TiMapUtil;
import top.ticho.tool.core.TiStrUtil;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 客户端权限消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientAuthMessageHandler extends AbstractClientMessageHandler {
    private final IntranetClientSupport intranetClientSupport;
    private final AtomicInteger serverStatus;

    public ClientAuthMessageHandler(IntranetServerHandler intranetServerHandler) {
        super(intranetServerHandler);
        this.intranetClientSupport = intranetServerHandler.intranetClientSupport();
        this.serverStatus = intranetServerHandler.serverStatus();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel clientChannel = ctx.channel();
        if (ServerStatus.STARTING.getCode() == serverStatus.get()) {
            log.info("服务未启用，请稍后再试，通道：{}", clientChannel);
            notifyStarting(clientChannel);
            return;
        }
        String accessKey = new String(message.data());
        Optional<IntranetClient> clientInfoOpt = intranetClientSupport.findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            String errorMsg = TiStrUtil.format("客户端[{}]不可用", accessKey);
            log.info(errorMsg);
            notifyError(clientChannel, errorMsg);
            return;
        }
        IntranetClient intranetClient = clientInfoOpt.get();
        Map<Integer, IntranetPort> portMap = intranetClient.getPortMap();
        if (TiMapUtil.isEmpty(portMap)) {
            log.info("客户端[{}]未绑定主机端口，通道：{}", accessKey, clientChannel);
            notifyError(clientChannel, TiStrUtil.format("客户端[{}]未绑定主机端口", accessKey));
            return;
        }
        Channel clientChannelGet = intranetClient.getChannel();
        if (IntranetUtil.isActive(clientChannelGet)) {
            log.info("客户端[{}]已经被其他客户端{}使用，通道：{}", accessKey, clientChannelGet, clientChannel);
            notifyError(clientChannel, TiStrUtil.format("客户端[{}]已经被其他客户端使用", accessKey));
            return;
        }
        notifySuccess(clientChannel, accessKey);
        String portStrs = portMap.keySet()
            .stream()
            .map(Objects::toString)
            .collect(Collectors.joining(","));
        // log.warn("[2]客户端[{}]成功连接，绑定端口{},客户端通道{}", accessKey, portStrs, clientChannel);
        log.info("客户端[{}]成功连接，绑定端口{}，通道：{}", accessKey, portStrs, clientChannel);
        clientChannel.attr(CommConst.REQUEST_ID_ATTR_MAP).set(new LinkedHashMap<>());
        intranetClient.connect(clientChannel);
    }

    private void notifySuccess(Channel channel, String accessKey) {
        notify(channel, Message.AUTH, TiStrUtil.format("客户端[{}]权限校验成功", accessKey, channel).getBytes(StandardCharsets.UTF_8));
    }

    private void notifyError(Channel channel, String errorMsg) {
        notify(channel, Message.DISABLED_ACCESS_KEY, errorMsg.getBytes(StandardCharsets.UTF_8));
    }

    private void notifyStarting(Channel channel) {
        notify(channel, Message.STARTING, "服务未启用，请稍后再试".getBytes(StandardCharsets.UTF_8));
    }

}
