package top.ticho.intranet.server.entity;

import lombok.Builder;
import lombok.Getter;
import top.ticho.intranet.common.enums.ProtocolType;

/**
 * 端口信息DTO
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Getter
@Builder
public class PortInfo {

    /** 客户端秘钥 */
    private String accessKey;
    /** 主机端口 */
    private Integer port;
    /** 客户端地址 */
    private String endpoint;
    /** 域名 */
    private String domain;
    /** 协议类型 */
    private Integer type;
    /** 协议类型名称 */
    private ProtocolType typeName;

}
