package top.ticho.starter.datasource.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;
import java.util.function.Predicate;

/**
 * mybatls 批量更新
 *
 * @author zhajianjun
 * @date 2022-10-17 09:13
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TiUpdateBatch extends AbstractMethod {
    /**
     * 字段筛选条件
     */
    @Accessors(chain = true)
    private Predicate<TableFieldInfo> predicate;

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        // id,username,age,
        String sqlColumn = tableInfo.getKeyColumn() + COMMA + this.filterTableFieldInfo(fieldList, this.predicate, TableFieldInfo::getInsertSqlColumn, "");
        // id,username,age
        String columns = sqlColumn.substring(0, sqlColumn.length() - 1);
        // #{et.id},#{et.username},#{et.age},
        String sqlProperty = SqlScriptUtils.safeParam(ENTITY_DOT + tableInfo.getKeyProperty()) + COMMA + this.filterTableFieldInfo(fieldList, predicate, i -> i.getInsertSqlProperty(ENTITY_DOT), EMPTY);
        // #{et.id},#{et.username},#{et.age}
        String property = "select " + sqlProperty.substring(0, sqlProperty.length() - 1);
        String valuesScript = SqlScriptUtils.convertForeach(property, "list", null, ENTITY, "union all");
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("<script>");
        sqlSb.append("update ").append(tableInfo.getTableName()).append(" as T1, (select ").append(columns).append(" from ").append(tableInfo.getTableName()).append(" where 1=2 union all");
        sqlSb.append(valuesScript);
        sqlSb.append(") as T2 set ");
        for (TableFieldInfo fieldInfo : fieldList) {
            sqlSb.append(" t1.").append(fieldInfo.getColumn()).append(" = t2.").append(fieldInfo.getColumn()).append(",");
        }
        sqlSb.deleteCharAt(sqlSb.length() - 1);
        sqlSb.append(" where ").append(" t1.").append(tableInfo.getKeyColumn()).append(" = t2.").append(tableInfo.getKeyColumn());
        if (tableInfo.isWithLogicDelete()) {
            sqlSb.append(" and t1.").append(tableInfo.getLogicDeleteFieldInfo().getColumn()).append(" = ").append(tableInfo.getLogicDeleteFieldInfo().getLogicNotDeleteValue());
        }
        sqlSb.append("</script>");
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlSb.toString(), modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, "updateBatch", sqlSource);
    }

}
