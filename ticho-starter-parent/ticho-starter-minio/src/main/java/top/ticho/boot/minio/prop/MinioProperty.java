package top.ticho.boot.minio.prop;

import lombok.Data;
import org.springframework.util.unit.DataSize;

/**
 * Miniio连接对象
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@Data
public class MinioProperty {

    /** 是否开启配置 */
    private Boolean enable;

    /** minio的地址 */
    private String endpoint;

    /** 用户名 */
    private String accessKey;

    /** 密码 */
    private String secretKey;

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
