package top.ticho.generator.keywords;

import top.ticho.generator.enums.DbType;

import java.util.EnumMap;
import java.util.Map;

/**
 * 关键字处理器注册表
 * <p>根据数据库类型维护对应的SQL关键字处理器映射关系</p>
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class KeyWordsRegistry {
    private final Map<DbType, KeyWordsHandler> keyWordsEnumMap = new EnumMap<>(DbType.class);

    public KeyWordsRegistry() {
        this.keyWordsEnumMap.put(DbType.MYSQL, new MySqlKeyWordsHandler());
    }

    public KeyWordsRegistry(DbType dbType, KeyWordsHandler keyWordsHandler) {
        this.keyWordsEnumMap.putIfAbsent(dbType, keyWordsHandler);
    }

    public KeyWordsHandler getKeyWordsHandler(DbType dbType) {
        return this.keyWordsEnumMap.get(dbType);
    }
}
