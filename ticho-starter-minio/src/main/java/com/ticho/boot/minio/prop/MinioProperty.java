package com.ticho.boot.minio.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

/**
 * Miniio连接对象
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@ConfigurationProperties(prefix = "ticho.minio")
@Component
@Data
public class MinioProperty {

    /**
     * minio的地址
     */
    private String endpoint;

    /**
     * 用户名
     */
    private String accessKey;

    /**
     * 密码
     */
    private String secretKey;

    /**
     * 图片大小限制，默认20MB
     */
    private DataSize maxImgSize = DataSize.ofMegabytes(20L);

    /**
     * 文件大小限制，默认20MB
     */
    private DataSize maxFileSize = DataSize.ofMegabytes(20L);

    /**
     * 默认桶
     */
    private String defaultBucket = "default";

}
