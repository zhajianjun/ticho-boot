package top.ticho.intranet.common.prop;

import lombok.Data;

/**
 * 客户端配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class ClientProperty {

    /** 秘钥 */
    private String accessKey;
    /** 服务地址 */
    private String serverHost;
    /** 服务端口 */
    private Integer serverPort;
    /** 服务是否开启ssl */
    private Boolean sslEnable;
    /** ssl证书路径 */
    private String sslPath;
    /** ssl证书密码 */
    private String sslPassword;
    /** 最大线程数量 */
    private Integer maxPoolSize = 100;
    /** 处理客户端连接线程数量 */
    private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * 是否尝试重连
     * 如果无效的访问密钥，是否尝试重连，是则重连，否则则关闭
     */
    private Boolean tryReconnect;

}
