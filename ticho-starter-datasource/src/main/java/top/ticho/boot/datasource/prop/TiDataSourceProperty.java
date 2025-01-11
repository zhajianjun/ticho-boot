package top.ticho.boot.datasource.prop;

import lombok.Data;

/**
 * @author zhajianjun
 * @date 2025-01-11 14:16
 */
@Data
public class TiDataSourceProperty {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private Integer typeAliasesPackage;
    private Log log;
    private Integer batchSize = 200;
    private Integer maxBatchSize = 1000;

    @Data
    public static class Log {
        private Boolean enable;
        private Boolean printSql;
        private Boolean showParams;
        private Boolean printSimple;
    }

}
