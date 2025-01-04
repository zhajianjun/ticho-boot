package top.ticho.boot.datasource.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
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
 * @author zhajianjun
 * @date 2025-01-04 19:50
 */
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsertOrUpdateBatch extends AbstractMethod {
    /**
     * 字段筛选条件
     */
    @Accessors(chain = true)
    private Predicate<TableFieldInfo> predicate;

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        // id,username,age,
        String sqlColumn = tableInfo.getKeyColumn() + Constants.COMMA + this.filterTableFieldInfo(fieldList, this.predicate, TableFieldInfo::getInsertSqlColumn, "");
        // id,username,age
        String columns = sqlColumn.substring(0, sqlColumn.length() - 1);
        // #{et.id},#{et.username},#{et.age},
        String sqlProperty = SqlScriptUtils.safeParam(ENTITY_DOT + tableInfo.getKeyProperty()) + COMMA + this.filterTableFieldInfo(fieldList, predicate, i -> i.getInsertSqlProperty(ENTITY_DOT), EMPTY);
        // (#{et.id},#{et.username},#{et.age})
        String property = LEFT_BRACKET + sqlProperty.substring(0, sqlProperty.length() - 1) + RIGHT_BRACKET;
        String valuesScript = SqlScriptUtils.convertForeach(property, "list", null, ENTITY, COMMA);
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("<script>");
        sqlSb.append("insert into ").append(tableInfo.getTableName()).append(" (").append(columns).append(") values ");
        sqlSb.append(valuesScript);
        sqlSb.append(" on duplicate key update ");
        for (TableFieldInfo fieldInfo : fieldList) {
            sqlSb.append(fieldInfo.getColumn()).append(" = values(").append(fieldInfo.getColumn()).append("),");
        }
        sqlSb.deleteCharAt(sqlSb.length() - 1);
        sqlSb.append("</script>");
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlSb.toString(), modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, "insertOrUpdateBatch", sqlSource);
    }


}
