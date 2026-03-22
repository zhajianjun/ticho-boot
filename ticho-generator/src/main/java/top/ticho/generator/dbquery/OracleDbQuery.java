package top.ticho.generator.dbquery;

/**
 * Oracle 数据库查询实现
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class OracleDbQuery implements DbQuery {

    @Override
    public String tablesSql() {
        return "SELECT * FROM ALL_TAB_COMMENTS WHERE OWNER='%s'";
    }

    @Override
    public String tableFieldsSql() {
        // 注意：此SQL中的 #schema 占位符需要在运行时替换为实际的 schema 名称
        // 查询字段包括：列名、数据类型、注释、主键标识、默认值、是否可为空
        return
            """
                SELECT A.COLUMN_NAME,
                    CASE WHEN A.DATA_TYPE='NUMBER' THEN
                      (CASE WHEN A.DATA_PRECISION IS NULL THEN A.DATA_TYPE
                       WHEN NVL(A.DATA_SCALE, 0) > 0 THEN A.DATA_TYPE||'('||A.DATA_PRECISION||','||A.DATA_SCALE||')'
                       ELSE A.DATA_TYPE||'('||A.DATA_PRECISION||')' END)
                    ELSE A.DATA_TYPE END DATA_TYPE,
                    B.COMMENTS,
                    DECODE(C.POSITION, '1', 'PRI') KEY,
                    A.DATA_DEFAULT,
                    A.NULLABLE
                FROM ALL_TAB_COLUMNS A
                    INNER JOIN ALL_COL_COMMENTS B ON A.TABLE_NAME = B.TABLE_NAME AND A.COLUMN_NAME = B.COLUMN_NAME AND B.OWNER = '#schema'
                    LEFT JOIN ALL_CONSTRAINTS D ON D.TABLE_NAME = A.TABLE_NAME AND D.CONSTRAINT_TYPE = 'P' AND D.OWNER = '#schema'
                    LEFT JOIN ALL_CONS_COLUMNS C ON C.CONSTRAINT_NAME = D.CONSTRAINT_NAME AND C.COLUMN_NAME=A.COLUMN_NAME AND C.OWNER = '#schema'
                WHERE A.OWNER = '#schema' AND A.TABLE_NAME = '%s'
                ORDER BY A.COLUMN_ID
                """;
    }

    @Override
    public String tableNameKey() {
        return "TABLE_NAME";
    }

    @Override
    public String tableCommentKey() {
        return "COMMENTS";
    }

    @Override
    public String fieldNameKey() {
        return "COLUMN_NAME";
    }

    @Override
    public String fieldTypeKey() {
        return "DATA_TYPE";
    }

    @Override
    public String fieldCommentKey() {
        return "COMMENTS";
    }

    @Override
    public String indexKey() {
        return "KEY";
    }

    @Override
    public String priKeyName() {
        return "PRI";
    }

    @Override
    public String defaultValue() {
        // Oracle 中默认值字段的列名是 DATA_DEFAULT
        return "DATA_DEFAULT";
    }

    @Override
    public String nullable() {
        // Oracle 中是否可为空字段的列名是 NULLABLE
        return "NULLABLE";
    }

    @Override
    public String nullableValue() {
        // Oracle 中 NULLABLE 为 'Y' 时表示可为空
        return "Y";
    }

}
