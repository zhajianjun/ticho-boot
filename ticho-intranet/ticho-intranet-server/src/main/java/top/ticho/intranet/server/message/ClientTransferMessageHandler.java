package top.ticho.intranet.server.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.entity.Message;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.core.IntranetServerHandler;

/**
 * 客户端信息传输消息处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientTransferMessageHandler extends AbstractClientMessageHandler {

    public ClientTransferMessageHandler(IntranetServerHandler intranetServerHandler) {
        super(intranetServerHandler);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message message) {
        Channel channel = ctx.channel();
        Channel requestChannel = channel.attr(CommConst.CHANNEL).get();
        if (!IntranetUtil.isActive(requestChannel)) {
            return;
        }
        ByteBuf data = ctx.alloc().buffer(message.data().length);
        data.writeBytes(message.data());
        requestChannel.writeAndFlush(data);
        // log.warn("[10][服务端]响应信息接收，接收通道：{}；写入通道：{}, 消息{}", channel, requestChannel, message);
    }

}
