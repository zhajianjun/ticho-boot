package top.ticho.tool.s3;

import lombok.Data;

/**
 * S3连接对象
 *
 * @author zhajianjun
 * @date 2025-10-29 19:27
 */
@Data
public class TiS3Property {

    /** 是否开启配置 */
    private Boolean enable;
    /** s3的地址 */
    private String endpoint;
    /** 用户名 */
    private String accessKey;
    /** 密码 */
    private String secretKey;
    /** 地区 */
    private String region;
    /** pathStyleAccess */
    private Boolean pathStyleAccess = true;
    /** 默认桶 */
    private String defaultBucket = "default";
    /** 分片桶 */
    private String chunkBucket = "chunk";
    /** 默认过期时间，单位(秒) */
    private Long defaultExpires = 5L;

}
