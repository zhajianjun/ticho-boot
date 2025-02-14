package top.ticho.starter.redisson.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Redisson连接方式
 * <p>
 * 包含:standalone-单节点部署方式
 * sentinel-哨兵部署方式
 * cluster-集群方式
 * masterslave-主从部署方式
 * </p>
 *
 * @author zhajianjun
 * @date 2023-02-17 16:42
 */
@Getter
@AllArgsConstructor
public enum TiRedissonType {

    /** 单节点部署方式 */
    STANDALONE("standalone", "单节点部署方式"),
    /** 哨兵部署方式 */
    SENTINEL("sentinel", "哨兵部署方式"),
    /** 集群部署方式 */
    CLUSTER("cluster", "集群方式"),
    /** 主从部署方式 */
    MASTERSLAVE("masterslave", "主从部署方式");

    private final String type;
    private final String desc;

}
