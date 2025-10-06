package top.ticho.intranet.server.listener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.TiIntranetConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.prop.IntranetServerProperty;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.core.IntranetServerHandler;
import top.ticho.intranet.server.entity.IntranetClient;
import top.ticho.intranet.server.entity.IntranetPort;
import top.ticho.intranet.server.support.IntranetApplicationSupport;
import top.ticho.intranet.server.support.IntranetClientSupport;
import top.ticho.tool.core.TiMapUtil;

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

    private final IntranetServerProperty intranetServerProperty;
    private final IntranetClientSupport intranetClientSupport;
    private final IntranetApplicationSupport intranetApplicationSupport;

    public AppRequestListener(IntranetServerHandler intranetServerHandler) {
        this.intranetServerProperty = intranetServerHandler.intranetServerProperty();
        this.intranetClientSupport = intranetServerHandler.intranetClientSupport();
        this.intranetApplicationSupport = intranetServerHandler.intranetApplicationSupport();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        // log.debug("[服务端]请求通道激活, {}", requestChannel);
        // 查询请求通道的端口号
        Integer portNum = IntranetUtil.getPortByChannel(requestChannel);
        // 查询客户端信息
        Optional<IntranetClient> clientInfoOpt = intranetClientSupport.findByPort(portNum);
        boolean isActive = clientInfoOpt.map(IntranetClient::getChannel).map(IntranetUtil::isActive).orElse(false);
        // 如果客户端连接异常，则关闭请求通道
        if (!isActive) {
            requestChannel.close();
            super.channelActive(ctx);
            return;
        }
        IntranetClient intranetClient = clientInfoOpt.get();
        Long maxRequests = intranetServerProperty.getMaxRequests();
        Channel clientChannel = intranetClient.getChannel();
        // 查询请求连接通道总数，超出最大值，则关闭第一个请求通道requestChannel
        Map<String, Channel> requestChannels = clientChannel.attr(TiIntranetConst.REQUEST_ID_ATTR_MAP).get();
        if (TiMapUtil.isNotEmpty(requestChannels) && requestChannels.size() >= maxRequests) {
            String firstKey = requestChannels.keySet().stream().findFirst().orElse(null);
            Channel oldRequestChannel = requestChannels.remove(firstKey);
            log.warn("超过最大连接数，关闭请求通道 {}", oldRequestChannel);
            IntranetUtil.close(oldRequestChannel);
        }
        String requestId = intranetApplicationSupport.getRequestId();
        // 请求通道自动读设置为false
        requestChannel.config().setOption(ChannelOption.AUTO_READ, false);
        // 请求通道添加请求连接id
        requestChannel.attr(TiIntranetConst.REQUEST_ID).set(requestId);
        requestChannels.put(requestId, requestChannel);
        // 获取端口信息
        IntranetPort port = intranetClient.getPortMap().get(portNum);
        Message message = new Message(Message.CONNECT, requestId, port.getEndpoint().getBytes());
        clientChannel.writeAndFlush(message);
        super.channelActive(ctx);
        // log.warn("[3][服务端]通道激活, 连接客户端{}, 消息{}", clientChannel, message);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) {
        Channel requestChannel = ctx.channel();
        // log.debug("[服务端]通道数据请求, 请求通道{}", requestChannel);
        Channel clientChannel = requestChannel.attr(TiIntranetConst.CHANNEL).get();
        if (!IntranetUtil.isActive(clientChannel)) {
            requestChannel.close();
            return;
        }
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        Message message = new Message(Message.TRANSFER, requestChannel.attr(TiIntranetConst.REQUEST_ID).get(), data);
        // log.warn("[7][服务端]请求传输到客户端，请求通道{}；客户端通道{}, 消息{}", requestChannel, clientChannel, message);
        clientChannel.writeAndFlush(message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        Integer portNum = IntranetUtil.getPortByChannel(requestChannel);
        Optional<IntranetClient> clientInfoOpt = intranetClientSupport.findByPort(portNum);
        boolean isActive = clientInfoOpt.map(IntranetClient::getChannel).map(IntranetUtil::isActive).orElse(false);
        if (!isActive) {
            requestChannel.close();
            super.channelInactive(ctx);
            return;
        }
        IntranetClient intranetClient = clientInfoOpt.get();
        Channel clientChannelGet = intranetClient.getChannel();
        String requestId = requestChannel.attr(TiIntranetConst.REQUEST_ID).get();
        intranetClientSupport.removeRequestChannel(clientChannelGet, requestId);
        Channel clientChannel = requestChannel.attr(TiIntranetConst.CHANNEL).get();
        if (!IntranetUtil.isActive(clientChannel)) {
            requestChannel.close();
            super.channelInactive(ctx);
            return;
        }
        IntranetUtil.close(clientChannel.attr(TiIntranetConst.CHANNEL).get());
        clientChannel.attr(TiIntranetConst.REQUEST_ID).set(null);
        clientChannel.attr(TiIntranetConst.ACCESS_KEY).set(null);
        clientChannel.attr(TiIntranetConst.CHANNEL).set(null);
        clientChannel.config().setOption(ChannelOption.AUTO_READ, true);
        Message message = new Message(Message.DISCONNECT, requestId, null);
        clientChannel.writeAndFlush(message);
        super.channelInactive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        Channel requestChannel = ctx.channel();
        Integer portNum = IntranetUtil.getPortByChannel(requestChannel);
        Optional<IntranetClient> clientInfoOpt = intranetClientSupport.findByPort(portNum);
        boolean isActive = clientInfoOpt.map(IntranetClient::getChannel).map(IntranetUtil::isActive).orElse(false);
        if (!isActive) {
            requestChannel.close();
        } else {
            Channel clientChannel = requestChannel.attr(TiIntranetConst.CHANNEL).get();
            if (Objects.nonNull(clientChannel)) {
                clientChannel.config().setOption(ChannelOption.AUTO_READ, requestChannel.isWritable());
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
