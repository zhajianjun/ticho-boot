package top.ticho.boot.datasource.prop;

import lombok.Data;

/**
 * 数据库配置
 *
 * @author zhajianjun
 * @date 2025-01-11 14:16
 */
@Data
public class TiDataSourceProperty {
    /** 数据库URL */
    private String url;
    /** 数据库用户名 */
    private String username;
    /** 数据库用户名密码 */
    private String password;
    /** 数据库驱动类名称 */
    private String driverClassName;
    /** 存储类型别名的包路径 */
    private Integer typeAliasesPackage;
    /** 存储日志相关的配置信息 */
    private Log log;
    /** 储批量操作的大小，默认值为200 */
    private Integer batchSize = 200;
    /** 存储最大批量操作的大小，默认值为1000 */
    private Integer maxBatchSize = 1000;

    @Data
    public static class Log {
        /** 是否启用日志 */
        private Boolean enable;
        /** 是否打印SQL语句 */
        private Boolean printSql;
        /** 是否显示SQL参数 */
        private Boolean showParams;
        /** 是否打印简化的日志信息 */
        private Boolean printSimple;
    }

}
