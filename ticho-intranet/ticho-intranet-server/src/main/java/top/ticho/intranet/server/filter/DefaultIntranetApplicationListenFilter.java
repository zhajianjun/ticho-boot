package top.ticho.intranet.server.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Data;
import top.ticho.intranet.server.entity.IntranetApplicationDataCollector;

import java.net.InetSocketAddress;

/**
 * 默认应用监听过滤器
 *
 * @author zhajianjun
 * @date 2024-05-14 17:22
 */
@Data
public class DefaultIntranetApplicationListenFilter implements IntranetApplicationListenFilter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        IntranetApplicationDataCollector collector = IntranetApplicationDataCollector.getCollector(addr.getPort());
        collector.getChannels().incrementAndGet();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        IntranetApplicationDataCollector collector = IntranetApplicationDataCollector.getCollector(addr.getPort());
        collector.incrementReadBytes(byteBuf.readableBytes());
        collector.incrementReadMsgs(1L);
    }

    @Override
    public void write(ChannelHandlerContext ctx, ByteBuf byteBuf, ChannelPromise promise) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        IntranetApplicationDataCollector collector = IntranetApplicationDataCollector.getCollector(addr.getPort());
        collector.incrementWriteBytes(byteBuf.readableBytes());
        collector.incrementWriteMsgs(1L);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress addr = (InetSocketAddress) ctx.channel().localAddress();
        IntranetApplicationDataCollector collector = IntranetApplicationDataCollector.getCollector(addr.getPort());
        collector.getChannels().decrementAndGet();
    }

}
