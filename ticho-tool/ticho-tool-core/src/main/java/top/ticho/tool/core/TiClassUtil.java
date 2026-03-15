package top.ticho.tool.core;

import top.ticho.tool.core.constant.TiCharConst;
import top.ticho.tool.core.constant.TiStrConst;
import top.ticho.tool.core.exception.TiUtilException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Java Class 工具类
 * <p>提供类加载、类型转换、基本类型与包装类型转换、类名处理等功能</p>
 *
 * @author zhajianjun
 * @date 2025-08-09 20:07
 */
public class TiClassUtil {
    /** 基本类型名称到类型的映射 */
    private static final Map<String, Class<?>> NAME_PRIMITIVE_MAP = new HashMap<>();
    /** 基本类型到包装类型的映射 */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>();
    /** 包装类型到基本类型的映射 */
    private static final Map<Class<?>, Class<?>> WRAPPER_PRIMITIVE_MAP = new HashMap<>();
    /** 基本类型到缩写的映射（如 Integer.TYPE -> "I"） */
    private static final Map<String, String> ABBREVIATION_MAP;
    /** 缩写到基本类型的映射（如 "I" -> Integer.TYPE.getName()） */
    private static final Map<String, String> REVERSE_ABBREVIATION_MAP;

    static {
        // 初始化基本类型映射表
        NAME_PRIMITIVE_MAP.put(Boolean.TYPE.getName(), Boolean.TYPE);
        NAME_PRIMITIVE_MAP.put(Byte.TYPE.getName(), Byte.TYPE);
        NAME_PRIMITIVE_MAP.put(Character.TYPE.getName(), Character.TYPE);
        NAME_PRIMITIVE_MAP.put(Double.TYPE.getName(), Double.TYPE);
        NAME_PRIMITIVE_MAP.put(Float.TYPE.getName(), Float.TYPE);
        NAME_PRIMITIVE_MAP.put(Integer.TYPE.getName(), Integer.TYPE);
        NAME_PRIMITIVE_MAP.put(Long.TYPE.getName(), Long.TYPE);
        NAME_PRIMITIVE_MAP.put(Short.TYPE.getName(), Short.TYPE);
        NAME_PRIMITIVE_MAP.put(Void.TYPE.getName(), Void.TYPE);
        // 初始化基本类型到包装类型的映射
        PRIMITIVE_WRAPPER_MAP.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_WRAPPER_MAP.put(Byte.TYPE, Byte.class);
        PRIMITIVE_WRAPPER_MAP.put(Character.TYPE, Character.class);
        PRIMITIVE_WRAPPER_MAP.put(Short.TYPE, Short.class);
        PRIMITIVE_WRAPPER_MAP.put(Integer.TYPE, Integer.class);
        PRIMITIVE_WRAPPER_MAP.put(Long.TYPE, Long.class);
        PRIMITIVE_WRAPPER_MAP.put(Double.TYPE, Double.class);
        PRIMITIVE_WRAPPER_MAP.put(Float.TYPE, Float.class);
        PRIMITIVE_WRAPPER_MAP.put(Void.TYPE, Void.TYPE);
        // 根据基本类型到包装类型的映射，构建包装类型到基本类型的映射
        PRIMITIVE_WRAPPER_MAP.forEach((primitiveClass, wrapperClass) -> {
            if (!primitiveClass.equals(wrapperClass)) {
                WRAPPER_PRIMITIVE_MAP.put(wrapperClass, primitiveClass);
            }
        });
        // 初始化基本类型缩写映射
        final Map<String, String> map = new HashMap<>();
        map.put(Integer.TYPE.getName(), "I");
        map.put(Boolean.TYPE.getName(), "Z");
        map.put(Float.TYPE.getName(), "F");
        map.put(Long.TYPE.getName(), "J");
        map.put(Short.TYPE.getName(), "S");
        map.put(Byte.TYPE.getName(), "B");
        map.put(Double.TYPE.getName(), "D");
        map.put(Character.TYPE.getName(), "C");
        ABBREVIATION_MAP = Collections.unmodifiableMap(map);
        REVERSE_ABBREVIATION_MAP = Collections.unmodifiableMap(map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));

    }

    /**
     * 判断指定类是否为简单值类型（基本类型或其包装类型）
     *
     * @param aClass 要判断的类
     * @return 如果是简单值类型则返回 true，否则返回 false
     */
    public static boolean isSimpleValueType(Class<?> aClass) {
        if (aClass == null) {
            return false;
        }
        return aClass.isPrimitive() || isPrimitiveWrapper(aClass);
    }

    /**
     * 判断指定类型是否为基本类型的包装类
     *
     * @param type 要判断的类型
     * @return 如果是包装类型则返回 true，否则返回 false
     */
    public static boolean isPrimitiveWrapper(final Class<?> type) {
        return WRAPPER_PRIMITIVE_MAP.containsKey(type);
    }


    /**
     * 加载指定名称的类
     *
     * @param className 类的全限定名
     * @param <T>       要加载的类类型
     * @return 加载后的 Class 对象
     * @throws TiUtilException 当类无法加载时抛出此异常
     */
    public static <T> Class<T> loadClass(String className) {
        Class<?> aClass;
        try {
            // 优先使用线程上下文类加载器，如果为空则使用当前类的类加载器
            final ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
            final ClassLoader classLoader = contextCL == null ? TiClassUtil.class.getClassLoader() : contextCL;
            aClass = getClass(classLoader, className, true);

        } catch (ClassNotFoundException e) {
            throw new TiUtilException(e);
        }
        return (Class<T>) aClass;
    }

    /**
     * 获取指定名称的类对象
     * <p>支持基本类型、数组类型以及内部类的类名解析</p>
     *
     * @param classLoader 类加载器
     * @param className   类名（可以是全限定名、基本类型名或数组类型名）
     * @param initialize  是否初始化类
     * @return 类对象
     * @throws ClassNotFoundException 当类无法找到时抛出此异常
     */
    public static Class<?> getClass(final ClassLoader classLoader, final String className, final boolean initialize) throws ClassNotFoundException {
        String next = className;
        int lastDotIndex;
        do {
            try {
                // 尝试获取基本类型
                final Class<?> clazz = getPrimitiveClass(next);
                return clazz != null ? clazz : java.lang.Class.forName(toCanonicalName(next), initialize, classLoader);
            } catch (final ClassNotFoundException ex) {
                // 如果未找到，尝试将 $ 替换为 . （处理内部类名称）
                lastDotIndex = next.lastIndexOf(TiCharConst.DOT);
                if (lastDotIndex != -1) {
                    next = next.substring(0, lastDotIndex) + TiCharConst.DL + next.substring(lastDotIndex + 1);
                }
            }
        } while (lastDotIndex != -1);
        throw new ClassNotFoundException(next);
    }

    /**
     * 获取类的简短类名（不包含包名）
     *
     * @param cls 要处理的类对象
     * @return 简短类名，如果类为 null 则返回空字符串
     */
    public static String getShortClassName(final Class<?> cls) {
        if (cls == null) {
            return TiStrConst.EMPTY;
        }
        return getShortClassName(cls.getName());
    }

    /**
     * 从完整类名中获取简短类名（不包含包名）
     * <p>支持数组类型和内部类的处理</p>
     *
     * @param className 完整的类名（包含包名）
     * @return 简短类名，如果输入为空则返回空字符串
     */
    public static String getShortClassName(String className) {
        if (TiStrUtil.isEmpty(className)) {
            return TiStrConst.EMPTY;
        }
        final StringBuilder arrayPrefix = new StringBuilder();
        // 处理数组类型
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }

            // 还原基本类型的缩写
            if (REVERSE_ABBREVIATION_MAP.containsKey(className)) {
                className = REVERSE_ABBREVIATION_MAP.get(className);
            }
        }
        // 提取最后一个点之后的部分作为简短类名
        final int lastDotIdx = className.lastIndexOf(TiCharConst.DOT);
        final int innerIdx = className.indexOf(TiCharConst.DL, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        // 将内部类分隔符 $ 替换为 .
        if (innerIdx != -1) {
            out = out.replace(TiCharConst.DL, TiCharConst.DOT);
        }
        return out + arrayPrefix;
    }

    /**
     * 根据类名获取基本类型的 Class 对象
     *
     * @param className 基本类型的名称
     * @return 基本类型的 Class 对象，如果不是基本类型则返回 null
     */
    private static Class<?> getPrimitiveClass(final String className) {
        return NAME_PRIMITIVE_MAP.get(className);
    }

    /**
     * 将类名转换为 JVM 内部使用的规范名称格式
     * <p>例如：将 "int[]" 转换为 "[I"，将 "java.lang.String[]" 转换为 "[Ljava.lang.String;"</p>
     *
     * @param className 要转换的类名
     * @return 规范化的类名
     */
    private static String toCanonicalName(final String className) {
        String canonicalName = TiStrUtil.deleteWhitespace(className);
        Objects.requireNonNull(canonicalName, "className");
        final String arrayMarker = "[]";
        if (canonicalName.endsWith(arrayMarker)) {
            final StringBuilder classNameBuffer = new StringBuilder();
            // 处理多维数组
            while (canonicalName.endsWith(arrayMarker)) {
                canonicalName = canonicalName.substring(0, canonicalName.length() - 2);
                classNameBuffer.append("[");
            }
            final String abbreviation = ABBREVIATION_MAP.get(canonicalName);
            if (abbreviation != null) {
                // 基本类型使用缩写
                classNameBuffer.append(abbreviation);
            } else {
                // 引用类型使用 L...; 格式
                classNameBuffer.append("L").append(canonicalName).append(";");
            }
            canonicalName = classNameBuffer.toString();
        }
        return canonicalName;
    }

}
