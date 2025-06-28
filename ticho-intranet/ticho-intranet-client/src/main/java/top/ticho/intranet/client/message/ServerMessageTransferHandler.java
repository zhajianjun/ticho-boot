package top.ticho.intranet.client.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;

/**
 * 服务端数据传输消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ServerMessageTransferHandler extends AbstractServerMessageHandler {

    public ServerMessageTransferHandler(IntranetClientHandler intranetClientHandler) {
        super(intranetClientHandler);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel serverChannel = ctx.channel();
        ByteBufAllocator alloc = ctx.alloc();
        Channel requestChannel = serverChannel.attr(CommConst.CHANNEL).get();
        if (requestChannel == null) {
            return;
        }
        ByteBuf buf = alloc.buffer(message.data().length);
        buf.writeBytes(message.data());
        requestChannel.writeAndFlush(buf);
        // log.warn("[8][客户端]接收到到客户端请求信息，接收通道{}，写入通道{}，消息{}", serverChannel, requestChannel, message);
    }

}
