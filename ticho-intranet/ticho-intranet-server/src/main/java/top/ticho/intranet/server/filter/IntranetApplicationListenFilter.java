package top.ticho.intranet.server.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * 应用监听过滤器 接口
 *
 * @author zhajianjun
 * @date 2024-05-14 18:18
 */
public interface IntranetApplicationListenFilter {

    void channelActive(ChannelHandlerContext ctx);

    void channelRead(ChannelHandlerContext ctx, ByteBuf byteBuf);

    void write(ChannelHandlerContext ctx, ByteBuf byteBuf, ChannelPromise promise);

    void channelInactive(ChannelHandlerContext ctx);

}
