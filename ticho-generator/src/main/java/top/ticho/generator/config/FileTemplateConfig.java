package top.ticho.generator.config;

import lombok.Data;

/**
 * 文件模板配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class FileTemplateConfig {

    /** 相对路径 */
    private String relativePath;
    /** 文件名前缀 */
    private String prefix;
    /** 文件名后缀 */
    private String suffix;
    /** 文件首字母是否小写 */
    private Boolean lowerFirstFileName = false;
    /** 文件是否创建 */
    private Boolean createFile = true;

}
