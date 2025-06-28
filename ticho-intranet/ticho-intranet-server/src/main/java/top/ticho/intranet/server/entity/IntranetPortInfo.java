package top.ticho.intranet.server.entity;

import lombok.Getter;

/**
 * 端口信息DTO
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Getter
public class IntranetPortInfo {

    /** 客户端秘钥 */
    private final String accessKey;
    /** 主机端口 */
    private final Integer port;
    /** 客户端地址 */
    private final String endpoint;

    public IntranetPortInfo(String accessKey, Integer port, String endpoint) {
        this.accessKey = accessKey;
        this.port = port;
        this.endpoint = endpoint;
    }

}
