package com.ticho.boot.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Json工具
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String EMPTY = "";

    private JsonUtil() {

    }

    static {
        setConfig(MAPPER);
    }

    public static void setConfig(ObjectMapper objectMapper) {
        // @formatter:off
        // 反序列化 默认遇到未知属性去时会抛一个JsonMappingException,所以关闭
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        /* 这个特性决定parser是否将允许使用非双引号属性名字， （这种形式在Javascript中被允许，但是JSON标准说明书中没有）。
         * 注意：由于JSON标准上需要为属性名称使用双引号，所以这也是一个非标准特性，默认是false的。
         * 同样，需要设置JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES为true，打开该特性。
         */
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        /*
         * 这个特性决定parser是否将允许使用非双引号属性名字， （这种形式在Javascript中被允许，但是JSON标准说明书中没有）。
         * 注意：由于JSON标准上需要为属性名称使用双引号，所以这也是一个非标准特性，默认是false的。
         * 同样，需要设置JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES为true，打开该特性。
         */
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DateFormatConsant.YYYY_MM_DD_HH_MM_SS)));
        javaTimeModule.addSerializer(LocalDate.class,new LocalDateSerializer(DateTimeFormatter.ofPattern(DateFormatConsant.YYYY_MM_DD)));
        javaTimeModule.addSerializer(LocalTime.class,new LocalTimeSerializer(DateTimeFormatter.ofPattern(DateFormatConsant.HH_MM_SS)));
        javaTimeModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DateFormatConsant.YYYY_MM_DD_HH_MM_SS)));
        javaTimeModule.addDeserializer(LocalDate.class,new LocalDateDeserializer(DateTimeFormatter.ofPattern(DateFormatConsant.YYYY_MM_DD)));
        javaTimeModule.addDeserializer(LocalTime.class,new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DateFormatConsant.HH_MM_SS)));
        objectMapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());
        // @formatter:on
    }

    /**
     * Object 转换 json格式的String
     *
     * @param obj Object
     * @return String
     */
    public static String toJsonString(Object obj) {
        try {
            if (obj instanceof String) {
                return obj.toString();
            }
            return Objects.nonNull(obj) ? MAPPER.writeValueAsString(obj) : EMPTY;
        } catch (Exception e) {
            log.error("toJsonString error, param={}, catch error {}", obj, e.getMessage(), e);
            return EMPTY;
        }
    }

    /**
     * json格式的String 转换成对象
     *
     * @param obj Object
     * @param clazz 该对象的类
     * @return T
     */
    public static <T> T toJavaObject(Object obj, Class<T> clazz) {
        checkNotNull(clazz);
        String jsonString;
        jsonString = objToString(obj);
        try {
            return isEmpty(jsonString) ? null : MAPPER.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("obj toJavaObject error, param={}, catch error {}", obj, e.getMessage(), e);
            return null;
        }
    }

    /**
     * json格式的String 转换成对象
     *
     * @param jsonString jsonString
     * @param clazz 该对象的类
     * @return T
     */
    public static <T> T toJavaObject(String jsonString, Class<T> clazz) {
        checkNotNull(clazz);
        try {
            return isEmpty(jsonString) ? null : MAPPER.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("str toJavaObject error, param={}, catch error {}", jsonString, e.getMessage(), e);
            return null;
        }
    }

    public static String objToString(Object obj) {
        String jsonString;
        if (obj instanceof String) {
            jsonString = (String) obj;
        } else {
            jsonString = toJsonString(obj);
        }
        return jsonString;
    }

    /**
     * json格式的String 转换成对象
     *
     * @param jsonStr jsonStr
     * @param typeReference 泛型类
     * @return T
     */
    public static <T> T toJavaObject(String jsonStr, TypeReference<T> typeReference) {
        checkNotNull(typeReference);
        try {
            return isEmpty(jsonStr) ? null : MAPPER.readValue(jsonStr, typeReference);
        } catch (Exception e) {
            log.error("str toJavaObject error, param={}, catch error {}", jsonStr, e.getMessage(), e);
            return null;
        }
    }

    /**
     * json格式的String 转换成对象
     *
     * @param obj Object
     * @param typeReference 泛型类
     * @return T
     */
    public static <T> T toJavaObject(Object obj, TypeReference<T> typeReference) {
        checkNotNull(typeReference);
        String jsonStr = objToString(obj);
        try {
            return isEmpty(jsonStr) ? null : MAPPER.readValue(jsonStr, typeReference);
        } catch (Exception e) {
            log.error("obj toJavaObject error, param={}, catch error {}", obj, e.getMessage(), e);
            return null;
        }
    }

    /**
     * json格式的String 转换成集合
     *
     * @param jsonStr String
     * @return T
     */
    public static List<Object> toList(String jsonStr) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, Object.class);
            return isEmpty(jsonStr) ? Collections.emptyList() : MAPPER.readValue(jsonStr, javaType);
        } catch (Exception e) {
            log.error("toList error, param={}, catch error {}", jsonStr, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * json格式的String 转换成集合,带泛型
     *
     * @param jsonStr String
     * @param clazz 集合的泛型对象类
     * @return List<T>
     */
    public static <T> List<T> toList(String jsonStr, Class<T> clazz) {
        clazz = Optional.ofNullable(clazz).orElseThrow(NullPointerException::new);
        try {

            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
            return isEmpty(jsonStr) ? Collections.emptyList() : MAPPER.readValue(jsonStr, javaType);
        } catch (Exception e) {
            log.error("toList error, param={}, catch error {}", jsonStr, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * json格式的String 转换成集合
     *
     * @param jsonStr Object
     * @return Map<Object, Object>
     */
    public static Map<Object, Object> toMap(String jsonStr) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(Map.class, Object.class, Object.class);
            return isEmpty(jsonStr) ? new LinkedHashMap<>() : MAPPER.readValue(jsonStr, javaType);
        } catch (Exception e) {
            log.error("toMap error, param={}, catch error {}", jsonStr, e.getMessage(), e);
            return new LinkedHashMap<>();
        }
    }

    /**
     * json格式的String 转换成集合,带泛型
     *
     * @param jsonStr Object
     * @param kClass Map K 的泛型对象类
     * @param vClass Map V 的泛型对象类
     * @return Map<K, V>
     */
    public static <K, V> Map<K, V> toMap(String jsonStr, Class<K> kClass, Class<V> vClass) {
        kClass = Optional.ofNullable(kClass).orElseThrow(NullPointerException::new);
        vClass = Optional.ofNullable(vClass).orElseThrow(NullPointerException::new);
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(Map.class, kClass, vClass);
            return isEmpty(jsonStr) ? new LinkedHashMap<>() : MAPPER.readValue(jsonStr, javaType);
        } catch (Exception e) {
            log.error("toMap error, param={}, catch error {}", jsonStr, e.getMessage(), e);
            return new LinkedHashMap<>();
        }
    }

    /**
     * 对象转Map
     *
     * @param obj Object
     * @return Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object obj) {
        try {
            if (obj instanceof Map) {
                return (Map<String, Object>) obj;
            }
            String jsonString = toJsonString(obj);
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            return isEmpty(jsonString) ? new LinkedHashMap<>() : MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {
            log.error("toMap exception {}", obj, e);
            return new LinkedHashMap<>();
        }
    }

    public static <T> T convert(Object obj, Class<T> clazz) {
        return obj != null ? toJavaObject(toJsonString(obj), clazz) : null;
    }

    public static <T> T convert(Object obj, TypeReference<T> typeReference) {
        return obj != null ? toJavaObject(toJsonString(obj), typeReference) : null;
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static void checkNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("param is not empty");
        }
    }
}
