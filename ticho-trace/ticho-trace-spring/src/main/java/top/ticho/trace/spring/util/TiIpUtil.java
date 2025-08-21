package top.ticho.trace.spring.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import top.ticho.tool.core.TiStrUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Stream;

/**
 * IP工具
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TiIpUtil {

    public static final String USER_AGENT = "User-Agent";

    private static final List<String> localhosts = Stream.of("127.0.0.1", "0:0:0:0:0:0:0:1").toList();


    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        // Proxy-Client-IP 这个一般是经过apache http服务器的请求才会有，用apache http做代理时一般会加上Proxy-Client-IP请求头，而WL-Proxy-Client-IP是他的weblogic插件加上的头。
        String unknown = "unknown";
        if (ip == null || ip.isEmpty() || unknown.equalsIgnoreCase(ip)) {
            // Proxy-Client-IP：apache 服务代理
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || unknown.equalsIgnoreCase(ip)) {
            // WL-Proxy-Client-IP：weblogic 服务代理
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || unknown.equalsIgnoreCase(ip)) {
            // HTTP_CLIENT_IP：有些代理服务器
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || unknown.equalsIgnoreCase(ip)) {
            // X-Real-IP：nginx服务代理
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (TiStrUtil.isBlank(ip)) {
            return "";
        }
        int index = ip.indexOf(",");
        if (index != -1) {
            return ip.substring(0, index);
        }
        if (!localhosts.contains(ip)) {
            return ip;
        }
        // 获取本机真正的ip地址
        return localIp();
    }

    public static String localIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

}
