package top.ticho.intranet.common.util;


import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.exception.IntranetException;

import java.net.InetSocketAddress;
import java.util.Objects;


/**
 * 内网穿透通用工具
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class IntranetUtil {
    private IntranetUtil() {
    }

    public static void close(Channel channel) {
        if (null == channel) {
            return;
        }
        try {
            channel.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static boolean isActive(Channel channel) {
        if (Objects.isNull(channel)) {
            return false;
        }
        return channel.isActive() || channel.isOpen();
    }

    public static void close(ChannelHandlerContext ctx) {
        if (null == ctx) {
            return;
        }
        try {
            close(ctx.channel());
            ctx.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void exec(String cmd) {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            log.error(StrUtil.format("执行命令失败：{}, 错误信息：｛｝", cmd, e.getMessage()), e);
        }
    }

    public static Integer getPortByChannel(Channel channel) {
        if (null == channel) {
            return null;
        }
        InetSocketAddress addr = (InetSocketAddress) channel.localAddress();
        return addr.getPort();
    }

    public static void isNotNull(Object obj, String message) {
        if (null == obj) {
            throw new IntranetException(message);
        }
    }

    public static void isNotEmpty(String obj, String message) {
        if (null == obj || obj.isEmpty()) {
            throw new IntranetException(message);
        }
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new IntranetException(message);
        }
    }

}
