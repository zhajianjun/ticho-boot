package com.ticho.boot.minio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * sevlet文件传输大小配置
 *
 * @author zhajianjun
 * @date 2022-09-13 15:54
 */
@Configuration
@PropertySource("classpath:DefaultSevletMultiartConfig.properties")
public class DefaultSevletMultiartConfig {
}
