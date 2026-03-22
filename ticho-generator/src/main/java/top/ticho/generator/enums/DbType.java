package top.ticho.generator.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * 数据库类型
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Getter
@AllArgsConstructor
public enum DbType {
    /** MYSQL */
    MYSQL("mysql", "MySql数据库"),
    /** ORACLE */
    ORACLE("oracle", "Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW)");

    /** 数据库名称 */
    private final String db;
    /** 描述 */
    private final String desc;

    /**
     * 根据驱动名称获取数据库类型
     *
     * @param driverName 驱动名称
     * @return 数据库类型，如果未找到则返回 {@link Optional#empty()}
     */
    public static Optional<DbType> getDbType(String driverName) {
        if (driverName == null) {
            return Optional.empty();
        }
        String lowerDriverName = driverName.toLowerCase();
        if (lowerDriverName.contains("oracle")) {
            return Optional.of(DbType.ORACLE);
        }
        if (lowerDriverName.contains("mysql")) {
            return Optional.of(DbType.MYSQL);
        }
        return Optional.empty();
    }

    /**
     * 根据驱动名称获取数据库类型，如果未找到则抛出异常
     *
     * @param driverName 驱动名称
     * @return 数据库类型
     * @throws IllegalArgumentException 如果未找到匹配的数据库类型
     */
    public static DbType getDbTypeOrThrow(String driverName) {
        return getDbType(driverName)
            .orElseThrow(() -> new IllegalArgumentException("Unsupported database type: " + driverName));
    }

}
