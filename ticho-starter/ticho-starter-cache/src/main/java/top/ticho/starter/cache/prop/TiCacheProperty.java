package top.ticho.starter.cache.prop;

import lombok.Data;

/**
 * @author zhajianjun
 * @date 2024-08-12 20:42
 */
@Data
public class TiCacheProperty {

    /** 是否开启缓存 */
    private Boolean enable;

    /** 默认名称 */
    private String name = "default";

    /** 过期时间，单位:秒(s) */
    private Integer ttl = 60 * 60;

    /** 最大存储数量 */
    private Integer maxSize = 1000;

}
