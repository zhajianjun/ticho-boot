package top.ticho.intranet.server.listener;

import cn.hutool.core.map.MapUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.prop.ServerProperty;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.core.ServerHandler;
import top.ticho.intranet.server.entity.ClientInfo;
import top.ticho.intranet.server.entity.PortInfo;
import top.ticho.intranet.server.support.ApplicationSupport;
import top.ticho.intranet.server.support.ClientSupport;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * App请求监听器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class AppRequestListener extends SimpleChannelInboundHandler<ByteBuf> {

    private final ServerProperty serverProperty;
    private final ClientSupport clientSupport;
    private final ApplicationSupport applicationSupport;

    public AppRequestListener(ServerHandler serverHandler) {
        this.serverProperty = serverHandler.serverProperty();
        this.clientSupport = serverHandler.clientSupport();
        this.applicationSupport = serverHandler.applicationSupport();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        // log.debug("[服务端]请求通道激活, {}", requestChannel);
        // 查询请求通道的端口号
        Integer portNum = IntranetUtil.getPortByChannel(requestChannel);
        // 查询客户端信息
        Optional<ClientInfo> clientInfoOpt = clientSupport.findByPort(portNum);
        boolean isActive = clientInfoOpt.map(ClientInfo::getChannel).map(IntranetUtil::isActive).orElse(false);
        // 如果客户端连接异常，则关闭请求通道
        if (!isActive) {
            requestChannel.close();
            super.channelActive(ctx);
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Long maxRequests = serverProperty.getMaxRequests();
        Channel clientChannel = clientInfo.getChannel();
        // 查询请求连接通道总数，超出最大值，则关闭第一个请求通道requestChannel
        Map<String, Channel> requestChannels = clientChannel.attr(CommConst.REQUEST_ID_ATTR_MAP).get();
        if (MapUtil.isNotEmpty(requestChannels) && requestChannels.size() >= maxRequests) {
            String firstKey = requestChannels.keySet().stream().findFirst().orElse(null);
            Channel oldRequestChannel = requestChannels.remove(firstKey);
            log.warn("超过最大连接数，关闭请求通道 {}", oldRequestChannel);
            IntranetUtil.close(oldRequestChannel);
        }
        String requestId = applicationSupport.getRequestId();
        // 请求通道自动读设置为false
        requestChannel.config().setOption(ChannelOption.AUTO_READ, false);
        // 请求通道添加请求连接id
        requestChannel.attr(CommConst.URI).set(requestId);
        requestChannels.put(requestId, requestChannel);
        // 获取端口信息
        PortInfo port = clientInfo.getPortMap().get(portNum);
        Message message = new Message();
        message.setType(Message.CONNECT);
        message.setUri(requestId);
        message.setData(port.getEndpoint().getBytes());
        clientChannel.writeAndFlush(message);
        super.channelActive(ctx);
        // log.warn("[3][服务端]通道激活, 连接客户端{}, 消息{}", clientChannel, message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) {
        Channel requestChannel = ctx.channel();
        // log.debug("[服务端]通道数据请求, 请求通道{}", requestChannel);
        Channel clientChannel = requestChannel.attr(CommConst.CHANNEL).get();
        if (!IntranetUtil.isActive(clientChannel)) {
            requestChannel.close();
            return;
        }
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        Message message = new Message();
        message.setType(Message.TRANSFER);
        message.setUri(requestChannel.attr(CommConst.URI).get());
        message.setData(data);
        // log.warn("[7][服务端]请求传输到客户端，请求通道{}；客户端通道{}, 消息{}", requestChannel, clientChannel, message);
        clientChannel.writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        Integer portNum = IntranetUtil.getPortByChannel(requestChannel);
        Optional<ClientInfo> clientInfoOpt = clientSupport.findByPort(portNum);
        boolean isActive = clientInfoOpt.map(ClientInfo::getChannel).map(IntranetUtil::isActive).orElse(false);
        if (!isActive) {
            requestChannel.close();
            super.channelInactive(ctx);
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Channel clientChannelGet = clientInfo.getChannel();
        String requestId = requestChannel.attr(CommConst.URI).get();
        clientSupport.removeRequestChannel(clientChannelGet, requestId);
        Channel clientChannel = requestChannel.attr(CommConst.CHANNEL).get();
        if (!IntranetUtil.isActive(clientChannel)) {
            requestChannel.close();
            super.channelInactive(ctx);
            return;
        }
        IntranetUtil.close(clientChannel.attr(CommConst.CHANNEL).get());
        clientChannel.attr(CommConst.URI).set(null);
        clientChannel.attr(CommConst.KEY).set(null);
        clientChannel.attr(CommConst.CHANNEL).set(null);
        clientChannel.config().setOption(ChannelOption.AUTO_READ, true);
        Message message = new Message();
        message.setType(Message.DISCONNECT);
        message.setUri(requestId);
        clientChannel.writeAndFlush(message);
        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        Integer portNum = IntranetUtil.getPortByChannel(requestChannel);
        Optional<ClientInfo> clientInfoOpt = clientSupport.findByPort(portNum);
        boolean isActive = clientInfoOpt.map(ClientInfo::getChannel).map(IntranetUtil::isActive).orElse(false);
        if (!isActive) {
            requestChannel.close();
        } else {
            Channel serverChannel = requestChannel.attr(CommConst.CHANNEL).get();
            if (Objects.nonNull(serverChannel)) {
                serverChannel.config().setOption(ChannelOption.AUTO_READ, requestChannel.isWritable());
            }
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端异常 {} {}", ctx.channel(), cause.getMessage());
        IntranetUtil.close(ctx);
    }

}
