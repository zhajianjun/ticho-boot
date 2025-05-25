package top.ticho.intranet.server.message;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.common.ServerStatus;
import top.ticho.intranet.server.core.ServerHandler;
import top.ticho.intranet.server.entity.ClientInfo;
import top.ticho.intranet.server.entity.PortInfo;
import top.ticho.intranet.server.repository.ClientRepository;

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
    private final ClientRepository clientRepository;
    private final AtomicInteger serverStatus;

    public ClientAuthMessageHandler(ServerHandler serverHandler) {
        super(serverHandler);
        this.clientRepository = serverHandler.clientRepository();
        this.serverStatus = serverHandler.serverStatus();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel clientChannel = ctx.channel();
        if (ServerStatus.STARTING.getCode() == serverStatus.get()) {
            log.info("服务未启用，请稍后再试，通道：{}", clientChannel);
            notifyStarting(clientChannel, message.getSerial());
            return;
        }
        String accessKey = message.getUri();
        Optional<ClientInfo> clientInfoOpt = clientRepository.findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            String errorMsg = StrUtil.format("秘钥[{}]的客户端不可用", accessKey);
            log.info(errorMsg);
            notifyError(clientChannel, errorMsg, message.getSerial());
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        if (MapUtil.isEmpty(portMap)) {
            log.info("秘钥[{}]的客户端未绑定主机端口，通道：{}", accessKey, clientChannel);
            notifyError(clientChannel, StrUtil.format("秘钥[{}]的客户端未绑定主机端口", accessKey), message.getSerial());
            return;
        }
        Channel clientChannelGet = clientInfo.getChannel();
        if (IntranetUtil.isActive(clientChannelGet)) {
            log.info("秘钥[{}]的客户端已经被其他客户端{}使用，通道：{}", accessKey, clientChannelGet, clientChannel);
            notifyError(clientChannel, StrUtil.format("秘钥[{}]的客户端已经被其他客户端使用", accessKey), message.getSerial());
            return;
        }
        notifySuccess(clientChannel, accessKey, message.getSerial());
        String portStrs = portMap.keySet()
            .stream()
            .map(Objects::toString)
            .collect(Collectors.joining(","));
        // log.warn("[2]秘钥[{}]的客户端成功连接，绑定端口{},客户端通道{}", accessKey, portStrs, clientChannel);
        log.info("秘钥[{}]的客户端成功连接，绑定端口{}，通道：{}", accessKey, portStrs, clientChannel);
        clientChannel.attr(CommConst.REQUEST_ID_ATTR_MAP).set(new LinkedHashMap<>());
        clientInfo.connect(clientChannel);
    }

    private void notifySuccess(Channel channel, String accessKey, long serial) {
        notify(channel, Message.AUTH, serial, StrUtil.format("秘钥[{}]的客户端权限校验成功", accessKey, channel).getBytes(StandardCharsets.UTF_8));
    }

    private void notifyError(Channel channel, String errorMsg, long serial) {
        notify(channel, Message.DISABLED_ACCESS_KEY, serial, errorMsg.getBytes(StandardCharsets.UTF_8));
    }

    private void notifyStarting(Channel channel, long serial) {
        notify(channel, Message.STARTING, serial, "服务未启用，请稍后再试".getBytes(StandardCharsets.UTF_8));
    }

}
