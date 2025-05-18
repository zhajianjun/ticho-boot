package top.ticho.intranet.server.entity;

import io.netty.channel.Channel;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 通道信息
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Getter
public class ClientInfo {

    /** 客户端秘钥 */
    private final String accessKey;
    /** 客户端名称 */
    private final String name;
    /** 端口信息(端口号, 端口对象信息) */
    private final Map<Integer, PortInfo> portMap;
    /** 连接时间 */
    private LocalDateTime connectTime;
    /** 连接的通道信息 */
    private transient Channel channel;

    public ClientInfo(String accessKey, String name) {
        this.accessKey = accessKey;
        this.name = name;
        this.portMap = new HashMap<>();
    }

    public void connect(Channel channel) {
        this.channel = channel;
        this.connectTime = LocalDateTime.now();
    }

    public void disconnect() {
        this.channel = null;
        this.connectTime = null;
    }

}
