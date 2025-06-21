package top.ticho.tool.json.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
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
import top.ticho.tool.json.constant.TiDateFormatConst;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Json工具
 *
 * @author zhajianjun
 * @date 2024-11-16 13:31
 */
public class TiJsonUtil {
    private static final Logger log = LoggerFactory.getLogger(TiJsonUtil.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectMapper MAPPER_YAML = new ObjectMapper(new YAMLFactory());
    private static final ObjectMapper MAPPER_PROPERTY = new ObjectMapper(new JavaPropsFactory());

    private TiJsonUtil() {
    }

    static {
        setConfig(MAPPER);
        setConfig(MAPPER_YAML);
        setConfig(MAPPER_PROPERTY);
    }

    /**
     * 注册模块（谨慎使用）
     *
     * @param module 模块
     */
    public static void registerModule(Module module) {
        MAPPER.registerModule(module);
        MAPPER_YAML.registerModule(module);
        MAPPER_PROPERTY.registerModule(module);
    }

    public static void setConfig(ObjectMapper objectMapper) {
        // 反序列化 默认遇到未知属性去时会抛一个JsonMappingException,所以关闭
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        /* 这个特性决定parser是否将允许使用非双引号属性名字， （这种形式在Javascript中被允许，但是JSON标准说明书中没有）。
         * 注意：由于JSON标准上需要为属性名称使用双引号，所以这也是一个非标准特性，默认是false的。
         * 同样，需要设置JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES为true，打开该特性。
         */
        objectMapper.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        /*
         * 这个特性决定parser是否将允许使用非双引号属性名字， （这种形式在Javascript中被允许，但是JSON标准说明书中没有）。
         * 注意：由于JSON标准上需要为属性名称使用双引号，所以这也是一个非标准特性，默认是false的。
         * 同样，需要设置JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES为true，打开该特性。
         */
        objectMapper.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        // 取消timestamps形式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略无法转换的对象
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // Jackson 将接受单个值作为数组。
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.setDateFormat(new SimpleDateFormat(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS));
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TiDateFormatConst.HH_MM_SS)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD_HH_MM_SS)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(TiDateFormatConst.YYYY_MM_DD)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TiDateFormatConst.HH_MM_SS)));
        objectMapper.registerModule(javaTimeModule).registerModule(new ParameterNamesModule());
    }

    /**
     * 对象转Json字符串
     */
    public static String toJsonString(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String string) {
            return string;
        }
        try {
            return MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Object to JsonString error, param={}, catch error {}", object, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 对象转Json字符串（格式化输出）
     */
    public static String toJsonStringPretty(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (object instanceof String string) {
            return string;
        }
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            log.error("Object to JsonStringPretty error, param={}, catch error {}", object, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Json字符串转对象
     */
    public static <T> T toObject(String jsonString) {
        if (isEmpty(jsonString)) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonString, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("JsonString to Object error, param={}, catch error {}", jsonString, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Json字符串转对象
     */
    public static <T> T toObject(String jsonString, Class<T> clazz) {
        checkNotNull(clazz);
        if (isEmpty(jsonString)) {
            return null;
        }
        try {
            return isEmpty(jsonString) ? null : MAPPER.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("JsonString to Object error, param={}, catch error {}", jsonString, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Json字符串转对象
     */
    public static <T> T toObject(String jsonString, TypeReference<T> typeReference) {
        checkNotNull(typeReference);
        if (isEmpty(jsonString)) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonString, typeReference);
        } catch (Exception e) {
            log.error("JsonString to Object error, param={}, catch error {}", jsonString, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Json字符串转对象
     */
    public static <T> T toObject(String jsonString, Class<?> parametrized, Class<?>... parameterClasses) {
        checkNotNull(parametrized);
        if (isEmpty(jsonString)) {
            return null;
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {
            log.error("JsonString to Object error, param={}, catch error {}", jsonString, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Json字符串转对象
     */
    public static <T> T toObject(Object object) {
        String jsonString = toJsonString(object);
        if (isEmpty(jsonString)) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonString, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Object to Object error, object={}, catch error {}", object, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Json字符串转对象
     */
    public static <T> T toObject(Object object, Class<T> clazz) {
        checkNotNull(clazz);
        String jsonString = toJsonString(object);
        if (isEmpty(jsonString)) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("Object to Object error, object={}, catch error {}", object, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Json字符串转对象
     *
     * @param object        Object
     * @param typeReference 泛型类
     * @return T
     */
    public static <T> T toObject(Object object, TypeReference<T> typeReference) {
        checkNotNull(typeReference);
        String jsonString = toJsonString(object);
        if (isEmpty(jsonString)) {
            return null;
        }
        try {
            return MAPPER.readValue(jsonString, typeReference);
        } catch (Exception e) {
            log.error("Object to Object error, param={}, catch error {}", object, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Json字符串转对象
     */
    public static <T> T toObject(Object object, Class<?> parametrized, Class<?>... parameterClasses) {
        checkNotNull(parametrized);
        String jsonString = toJsonString(object);
        if (isEmpty(jsonString)) {
            return null;
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {
            log.error("Object to Object error, param={}, catch error {}", object, e.getMessage(), e);
            return null;
        }
    }


    /**
     * Json字符串转集合
     */
    public static List<Object> toList(String jsonString) {
        if (isEmpty(jsonString)) {
            return new ArrayList<>();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, Object.class);
            return MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {
            log.error("JsonString to List error, param={}, catch error {}", jsonString, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Json字符串转集合,带泛型
     */
    public static <T> List<T> toList(String jsonString, Class<T> clazz) {
        checkNotNull(clazz);
        if (isEmpty(jsonString)) {
            return new ArrayList<>();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
            return MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {
            log.error("JsonString to List error, param={}, catch error {}", jsonString, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Json字符串转Map集合
     */
    public static Map<Object, Object> toMap(String jsonString) {
        if (isEmpty(jsonString)) {
            return new LinkedHashMap<>();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(Map.class, Object.class, Object.class);
            return isEmpty(jsonString) ? new LinkedHashMap<>() : MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {
            log.error("JsonString to Map error, param={}, catch error {}", jsonString, e.getMessage(), e);
            return new LinkedHashMap<>();
        }
    }

    /**
     * Json字符串转集合,带泛型
     */
    public static <K, V> Map<K, V> toMap(String jsonString, Class<K> kClass, Class<V> vClass) {
        checkNotNull(kClass);
        checkNotNull(vClass);
        if (isEmpty(jsonString)) {
            return new LinkedHashMap<>();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(Map.class, kClass, vClass);
            return MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {
            log.error("JsonString to Map error, param={}, catch error {}", jsonString, e.getMessage(), e);
            return new LinkedHashMap<>();
        }
    }

    /**
     * 对象转Map
     */
    public static Map<String, Object> toMap(Object object) {
        String jsonString = toJsonString(object);
        if (isEmpty(jsonString)) {
            return new LinkedHashMap<>();
        }
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(Map.class, String.class, Object.class);
            return isEmpty(jsonString) ? new LinkedHashMap<>() : MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {
            log.error("Object to Map exception {}", object, e);
            return new LinkedHashMap<>();
        }
    }

    /**
     * 转JsonNode
     */
    public static JsonNode toJsonNode(Object object) {
        try {
            return MAPPER.valueToTree(object);
        } catch (Exception e) {
            log.error("Object to JsonNode exception {}", object, e);
            return NullNode.getInstance();
        }
    }

    /**
     * 判断字符串是否为json
     */
    public static boolean isJson(String jsonStr) {
        if (isEmpty(jsonStr)) {
            return false;
        }
        JsonNode jsonNode = toJsonNode(jsonStr);
        return Objects.nonNull(jsonNode) && !jsonNode.isNull();
    }

    /**
     * 深拷贝
     */
    public static <T> T copy(Object object) {
        return toObject(object);
    }

    /**
     * 深拷贝
     */
    public static <T> T copy(Object object, Class<T> clazz) {
        return toObject(object, clazz);
    }

    /**
     * 深拷贝
     */
    public static <T> T copy(Object object, TypeReference<T> typeReference) {
        return toObject(object, typeReference);
    }

    /**
     * 深拷贝
     */
    public static <T> T copy(Object object, Class<?> parametrized, Class<?>... parameterClasses) {
        return toObject(object, parametrized, parameterClasses);
    }

    public static <T> T toObjectFromYaml(File file, Class<T> clazz) {
        checkNotNull(clazz);
        try {
            if (!file.exists()) {
                return null;
            }
            return MAPPER_YAML.readValue(file, clazz);
        } catch (Exception e) {
            log.error("File to Object From Yaml exception {}", file, e);
            return null;
        }
    }

    public static <T> T toObjectFromYaml(File file, TypeReference<T> typeReference) {
        checkNotNull(typeReference);
        try {
            if (!file.exists()) {
                return null;
            }
            return MAPPER_YAML.readValue(file, typeReference);
        } catch (Exception e) {
            log.error("File to Object From Yaml exception {}", file, e);
            return null;
        }
    }

    public static <T> T toObjectFromYaml(File file, Class<?> parametrized, Class<?>... parameterClasses) {
        checkNotNull(parametrized);
        try {
            if (!file.exists()) {
                return null;
            }
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return MAPPER_YAML.readValue(file, javaType);
        } catch (Exception e) {
            log.error("File to Object From Yaml exception {}", file, e);
            return null;
        }
    }

    public static <T> T toObjectFromProperty(File file, Class<T> clazz) {
        checkNotNull(clazz);
        try {
            if (!file.exists()) {
                return null;
            }
            return MAPPER_PROPERTY.readValue(file, clazz);
        } catch (Exception e) {
            log.error("File to Object From Property exception {}", file, e);
            return null;
        }
    }

    public static <T> T toObjectFromProperty(File file, TypeReference<T> typeReference) {
        checkNotNull(typeReference);
        try {
            if (!file.exists()) {
                return null;
            }
            return MAPPER_PROPERTY.readValue(file, typeReference);
        } catch (Exception e) {
            log.error("File to Object From Property exception {}", file, e);
            return null;
        }
    }

    public static <T> T toObjectFromProperty(File file, Class<?> parametrized, Class<?>... parameterClasses) {
        checkNotNull(parametrized);
        try {
            if (!file.exists()) {
                return null;
            }
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
            return MAPPER_PROPERTY.readValue(file, javaType);
        } catch (Exception e) {
            log.error("File to Object From Property exception {}", file, e);
            return null;
        }
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static void checkNotNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("param is not empty");
        }
    }

    public static void checkNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

}
