package top.ticho.starter.datasource.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.Setter;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-01-04 19:50
 */
@Setter
public class TiInsertOrUpdate extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        String keyColumn = tableInfo.getKeyColumn();
        String keyProperty = tableInfo.getKeyProperty();
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("<script>");
        sqlSb.append("insert into ").append(tableInfo.getTableName());
        sqlSb.append("<trim prefix='(' suffix=')' suffixOverrides=','>");
        sqlSb.append(SqlScriptUtils.convertIf(keyColumn + COMMA, String.format("%s != null", keyProperty), false));
        for (TableFieldInfo fieldInfo : fieldList) {
            sqlSb.append(SqlScriptUtils.convertIf(fieldInfo.getColumn() + COMMA, String.format("%s != null", fieldInfo.getProperty()), false));
        }
        sqlSb.append("</trim>");
        sqlSb.append("values");
        sqlSb.append("<trim prefix='(' suffix=')' suffixOverrides=','>");
        sqlSb.append(SqlScriptUtils.convertIf(SqlScriptUtils.safeParam(keyProperty) + COMMA, String.format("%s != null", keyProperty), false));
        for (TableFieldInfo fieldInfo : fieldList) {
            sqlSb.append(SqlScriptUtils.convertIf(SqlScriptUtils.safeParam(fieldInfo.getProperty()) + COMMA, String.format("%s != null", fieldInfo.getProperty()), false));
        }
        sqlSb.append("</trim>");
        sqlSb.append(" on duplicate key update ");
        sqlSb.append("<trim suffixOverrides=','>");
        for (TableFieldInfo fieldInfo : fieldList) {
            String sqlScript = String.format("%s=values(%s),", fieldInfo.getColumn(), fieldInfo.getColumn());
            String ifTest = String.format("%s != null", fieldInfo.getProperty());
            sqlSb.append(SqlScriptUtils.convertIf(sqlScript, ifTest, false));
        }
        sqlSb.append("</trim>");
        sqlSb.append("</script>");
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sqlSb.toString(), modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, "insertOrUpdate", sqlSource);
    }


}
