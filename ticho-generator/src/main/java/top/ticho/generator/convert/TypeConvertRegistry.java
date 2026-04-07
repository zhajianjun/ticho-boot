package top.ticho.generator.convert;

import top.ticho.generator.enums.DbType;

import java.util.EnumMap;
import java.util.Map;

/**
 * 类型转换器注册表
 * <p>根据数据库类型维护对应的数据库类型到Java类型转换器映射关系</p>
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class TypeConvertRegistry {

    private final Map<DbType, TypeConverter> typeConvertEnumMap = new EnumMap<>(DbType.class);

    public TypeConvertRegistry() {
        this.typeConvertEnumMap.put(DbType.MYSQL, new MySqlTypeConverter());
    }

    public TypeConverter getTypeConvert(DbType dbType) {
        return this.typeConvertEnumMap.get(dbType);
    }

}
