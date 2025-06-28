package top.ticho.intranet.server.filter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 应用监听过滤器处理器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class IntranetApplicationListenProxyFilter extends ChannelDuplexHandler {
    private final List<IntranetApplicationListenFilter> appDataListens = new ArrayList<>();

    public IntranetApplicationListenProxyFilter register(IntranetApplicationListenFilter appDataListen) {
        if (Objects.isNull(appDataListen)) {
            return this;
        }
        appDataListens.add(appDataListen);
        return this;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        appDataListens.forEach(appDataListen -> appDataListen.channelActive(ctx));
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object byteBuf) {
        appDataListens.forEach(appDataListen -> appDataListen.channelRead(ctx, (ByteBuf) byteBuf));
        ctx.fireChannelRead(byteBuf);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object byteBuf, ChannelPromise promise) throws Exception {
        appDataListens.forEach(appDataListen -> appDataListen.write(ctx, (ByteBuf) byteBuf, promise));
        super.write(ctx, byteBuf, promise);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        appDataListens.forEach(appDataListen -> appDataListen.channelInactive(ctx));
        super.channelInactive(ctx);
    }

}
