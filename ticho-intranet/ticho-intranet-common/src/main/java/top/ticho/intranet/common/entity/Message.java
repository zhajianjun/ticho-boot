package top.ticho.intranet.common.entity;

/**
 * 消息体
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public record Message(
    /* 类型 */
    byte type,
    /* requestId */
    String requestId,
    /* 数据 */
    byte[] data
) {

    /** 1-验证消息以检查accessKey是否正确 */
    public static final byte AUTH = 1;
    /** 2-无效的访问密钥 */
    public static final byte DISABLED_ACCESS_KEY = 2;
    /** 3-客户端通道连接 */
    public static final byte CONNECT = 3;
    /** 4-客户端断开通道连接 */
    public static final byte DISCONNECT = 4;
    /** 5-数据传输 */
    public static final byte TRANSFER = 5;
    /** 6-客户端心跳 */
    public static final byte HEARTBEAT = 6;
    /** 7-服务启动中 */
    public static final byte STARTING = 7;

}
