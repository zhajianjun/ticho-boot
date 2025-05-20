package top.ticho.intranet.client.handler;

import io.netty.channel.nio.NioEventLoopGroup;
import top.ticho.intranet.client.repository.AppReposipory;
import top.ticho.intranet.client.repository.ServerRepository;
import top.ticho.intranet.common.prop.ClientProperty;

/**
 * @author zhajianjun
 * @date 2025-05-20 22:48
 */
public record ClientContext(
    NioEventLoopGroup workerGroup,
    ClientProperty clientProperty,
    ServerRepository serverRepository,
    AppReposipory appReposipory
) {
}
