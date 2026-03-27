package top.ticho.starter.datasource.interceptor;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import top.ticho.starter.datasource.prop.TiDataSourceProperty;
import top.ticho.tool.core.TiClassUtil;
import top.ticho.tool.core.TiCollUtil;
import top.ticho.tool.core.TiLocalDateTimeUtil;
import top.ticho.tool.core.TiNumberUtil;
import top.ticho.tool.core.TiObjUtil;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.json.util.TiJsonUtil;

import java.sql.Statement;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;


/**
 * sql日志打印
 *
 * @author zhajianjun
 * @date 2022-09-19 14:17
 */
@Intercepts({
    @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
    @Signature(type = StatementHandler.class, method = "update", args = Statement.class),
    @Signature(type = StatementHandler.class, method = "batch", args = Statement.class)
})
@Slf4j
@Component
@ConditionalOnProperty(value = "ticho.datasource.log.enable", havingValue = "true")
public class TiSqlLogInterceptor implements Interceptor {
    /**
     * 默认敏感字段列表
     */
    private static final Set<String> DEFAULT_SENSITIVE_FIELDS = new HashSet<>(Arrays.asList(
        "password", "pwd", "passwd", "secret", "token",
        "phone", "mobile", "idCard", "idcard", "id_card"
    ));

    /**
     * 敏感字段集合
     */
    private final Set<String> sensitiveFields;
    private final TiDataSourceProperty tiDataSourceProperty;

    public TiSqlLogInterceptor(TiDataSourceProperty tiDataSourceProperty) {
        this.tiDataSourceProperty = tiDataSourceProperty;
        this.sensitiveFields = initSensitiveFields();
    }

    /**
     * 初始化敏感字段集合
     */
    private Set<String> initSensitiveFields() {
        Set<String> fields = new HashSet<>(DEFAULT_SENSITIVE_FIELDS);
        TiDataSourceProperty.Log logConfig = tiDataSourceProperty.getLog();
        if (logConfig != null && TiStrUtil.isNotBlank(logConfig.getSensitiveFields())) {
            String[] customFields = logConfig.getSensitiveFields().split(",");
            for (String field : customFields) {
                String trimmedField = field.trim().toLowerCase();
                if (TiStrUtil.isNotBlank(trimmedField)) {
                    fields.add(trimmedField);
                }
            }
        }
        return fields;
    }

    /**
     * 拦截类型StatementHandler
     */
    @Override
    public Object plugin(Object target) {
        // 注意当前判断的类型是我们intercept方法所拦截的类型
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        TiDataSourceProperty.Log tiDataSourcePropertyLog = tiDataSourceProperty.getLog();
        if (Objects.isNull(tiDataSourcePropertyLog) || !Boolean.TRUE.equals(tiDataSourcePropertyLog.getPrintSql())) {
            return invocation.proceed();
        }
        Object target = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(target);
        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        MybatisConfiguration mybatisConfiguration = (MybatisConfiguration) metaObject.getValue("delegate.configuration");
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        String sql = getSql(mybatisConfiguration, boundSql);
        long start = SystemClock.now();
        Object result = invocation.proceed();
        long timing = SystemClock.now() - start;
        int length;
        if (Objects.isNull(result)) {
            length = -1;
        } else if (TiClassUtil.isSimpleValueType(result.getClass())) {
            if (TiNumberUtil.isNumber(result.toString())) {
                length = TiNumberUtil.parseInt(result.toString());
            } else {
                length = 1;
            }
        } else {
            length = TiObjUtil.length(result);
        }
        if (Boolean.TRUE.equals(tiDataSourcePropertyLog.getPrintSimple())) {
            log.info("[SQL]【{}】-【{}】-【计数:{}】-【耗时:{}ms】", ms.getId(), sql, length, timing);
        } else {
            log.info("[SQL]【{}】-【{}】-【计数:{}】-【耗时:{}ms】-【记录:{}】", ms.getId(), sql, length, timing, TiJsonUtil.toJsonString(result));
        }
        return result;
    }


    public String getSql(Configuration configuration, BoundSql boundSql) {
        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // sql语句中多个空格都用一个空格代替
        String sql = boundSql.getSql().replaceAll("\\s+", " ");
        TiDataSourceProperty.Log tiDataSourcePropertyLog = tiDataSourceProperty.getLog();
        if (!Boolean.TRUE.equals(tiDataSourcePropertyLog.getShowParams())) {
            return sql;
        }
        if (TiCollUtil.isEmpty(parameterMappings) || Objects.isNull(parameterObject)) {
            return sql;
        }
        // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
        if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            return sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(null, parameterObject)));
        }
        // MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
        MetaObject metaObject = configuration.newMetaObject(parameterObject);
        for (ParameterMapping parameterMapping : parameterMappings) {
            String propertyName = parameterMapping.getProperty();
            if (metaObject.hasGetter(propertyName)) {
                Object obj = metaObject.getValue(propertyName);
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(propertyName, obj)));
            } else if (boundSql.hasAdditionalParameter(propertyName)) {
                // 该分支是动态sql
                Object obj = boundSql.getAdditionalParameter(propertyName);
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(propertyName, obj)));
            } else {
                // 打印出缺失，提醒该参数缺失并防止错位
                sql = sql.replaceFirst("\\?", "缺失");
            }
        }
        return sql;
    }

    /**
     * 获取参数值，并对敏感字段进行脱敏处理
     * <p>
     * 如果参数是String，则添加单引号，
     * 如果是日期，则转换为时间格式器并加单引号；
     * 对参数是null和不是null的情况作了处理
     * </p>
     *
     * @param propertyName 字段名
     * @param obj          参数值
     * @return 处理后的参数值字符串
     */
    private String getParameterValue(String propertyName, Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String str) {
            String value = str.isEmpty() ? "" : str;
            // 对敏感字段进行脱敏处理
            if (isSensitiveField(propertyName)) {
                value = maskSensitiveValue(value);
            }
            return value.isEmpty() ? "''" : "'" + value + "'";
        }
        if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            return "'" + formatter.format(obj) + "'";
        }
        if (obj instanceof LocalDateTime localDateTime) {
            return "'" + TiLocalDateTimeUtil.formatNormal(localDateTime) + "'";
        }
        if (obj instanceof LocalDate localDate) {
            return "'" + TiLocalDateTimeUtil.formatNormal(localDate) + "'";
        }
        if (obj instanceof LocalTime localTime) {
            return "'" + TiLocalDateTimeUtil.formatNormal(localTime) + "'";
        }
        return obj.toString();
    }

    /**
     * 判断字段是否为敏感字段
     *
     * @param fieldName 字段名
     * @return 是否为敏感字段
     */
    private boolean isSensitiveField(String fieldName) {
        if (TiStrUtil.isBlank(fieldName)) {
            return false;
        }
        String lowerFieldName = fieldName.toLowerCase();
        for (String sensitiveField : sensitiveFields) {
            if (lowerFieldName.contains(sensitiveField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对敏感值进行脱敏处理
     * <p>
     * 脱敏规则：
     * <ul>
     *     <li>长度 <= 2：显示前1位，其余用*代替</li>
     *     <li>长度 3-4：显示前1位和后1位，中间用*代替</li>
     *     <li>长度 >= 5：显示前2位和后2位，中间用*代替</li>
     * </ul>
     * </p>
     *
     * @param value 原始值
     * @return 脱敏后的值
     */
    private String maskSensitiveValue(String value) {
        if (TiStrUtil.isBlank(value)) {
            return value;
        }
        int length = value.length();
        if (length <= 2) {
            return value.charAt(0) + "*".repeat(Math.max(0, length - 1));
        } else if (length <= 4) {
            return value.charAt(0) + "*" + value.charAt(length - 1);
        } else {
            return value.substring(0, 2) + "****" + value.substring(length - 2);
        }
    }

}
