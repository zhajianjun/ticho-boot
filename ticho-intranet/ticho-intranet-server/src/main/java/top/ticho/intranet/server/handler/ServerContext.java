package top.ticho.intranet.server.handler;

import io.netty.channel.nio.NioEventLoopGroup;
import top.ticho.intranet.common.prop.ServerProperty;
import top.ticho.intranet.server.filter.AppListenFilter;
import top.ticho.intranet.server.repository.AppReposipory;
import top.ticho.intranet.server.repository.ClientRepository;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhajianjun
 * @date 2025-05-18 11:21
 */
public record ServerContext(
    AtomicInteger serverStatus,
    NioEventLoopGroup serverBoss,
    NioEventLoopGroup serverWorker,
    ServerProperty serverProperty,
    ClientRepository clientRepository,
    AppReposipory appReposipory,
    AppListenFilter appListenFilter
) {

}
