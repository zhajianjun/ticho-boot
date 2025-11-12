package top.ticho.tool.s3;

import lombok.Data;
import top.ticho.tool.core.unit.DataSize;

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
    /** 图片大小限制，默认20MB */
    private DataSize maxImgSize = DataSize.ofMegabytes(20L);
    /** 文件大小限制，默认20MB */
    private DataSize maxFileSize = DataSize.ofMegabytes(20L);
    /** 分段上传大小，最小5MB,最大5GB */
    private Long partSize = 5 * 1024 * 1024L;
    /** 默认桶 */
    private String defaultBucket = "default";
    /** 分片桶 */
    private String chunkBucket = "chunk";

}
