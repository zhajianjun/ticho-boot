package top.ticho.intranet.server.entity;

/**
 * 端口
 *
 * @param accessKey 客户端秘钥
 * @param port      主机端口
 * @param endpoint  客户端地址
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public record IntranetPort(String accessKey, Integer port, String endpoint) {

}
