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
 *
 *
 * @author zhajianjun
 * @date 2025-08-09 20:07
 */
public class TiClassUtil {
    private static final Map<String, Class<?>> NAME_PRIMITIVE_MAP = new HashMap<>();
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>();
    private static final Map<Class<?>, Class<?>> WRAPPER_PRIMITIVE_MAP = new HashMap<>();
    private static final Map<String, String> ABBREVIATION_MAP;
    private static final Map<String, String> REVERSE_ABBREVIATION_MAP;

    static {
        NAME_PRIMITIVE_MAP.put(Boolean.TYPE.getName(), Boolean.TYPE);
        NAME_PRIMITIVE_MAP.put(Byte.TYPE.getName(), Byte.TYPE);
        NAME_PRIMITIVE_MAP.put(Character.TYPE.getName(), Character.TYPE);
        NAME_PRIMITIVE_MAP.put(Double.TYPE.getName(), Double.TYPE);
        NAME_PRIMITIVE_MAP.put(Float.TYPE.getName(), Float.TYPE);
        NAME_PRIMITIVE_MAP.put(Integer.TYPE.getName(), Integer.TYPE);
        NAME_PRIMITIVE_MAP.put(Long.TYPE.getName(), Long.TYPE);
        NAME_PRIMITIVE_MAP.put(Short.TYPE.getName(), Short.TYPE);
        NAME_PRIMITIVE_MAP.put(Void.TYPE.getName(), Void.TYPE);

        PRIMITIVE_WRAPPER_MAP.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_WRAPPER_MAP.put(Byte.TYPE, Byte.class);
        PRIMITIVE_WRAPPER_MAP.put(Character.TYPE, Character.class);
        PRIMITIVE_WRAPPER_MAP.put(Short.TYPE, Short.class);
        PRIMITIVE_WRAPPER_MAP.put(Integer.TYPE, Integer.class);
        PRIMITIVE_WRAPPER_MAP.put(Long.TYPE, Long.class);
        PRIMITIVE_WRAPPER_MAP.put(Double.TYPE, Double.class);
        PRIMITIVE_WRAPPER_MAP.put(Float.TYPE, Float.class);
        PRIMITIVE_WRAPPER_MAP.put(Void.TYPE, Void.TYPE);

        PRIMITIVE_WRAPPER_MAP.forEach((primitiveClass, wrapperClass) -> {
            if (!primitiveClass.equals(wrapperClass)) {
                WRAPPER_PRIMITIVE_MAP.put(wrapperClass, primitiveClass);
            }
        });

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

    public static boolean isSimpleValueType(Class<?> aClass) {
        if (aClass == null) {
            return false;
        }
        return aClass.isPrimitive() || isPrimitiveWrapper(aClass);
    }

    public static boolean isPrimitiveWrapper(final Class<?> type) {
        return WRAPPER_PRIMITIVE_MAP.containsKey(type);
    }


    public static <T> Class<T> loadClass(String className) {
        Class<?> aClass;
        try {
            final ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
            final ClassLoader classLoader = contextCL == null ? TiClassUtil.class.getClassLoader() : contextCL;
            aClass = getClass(classLoader, className, true);

        } catch (ClassNotFoundException e) {
            throw new TiUtilException(e);
        }
        return (Class<T>) aClass;
    }

    public static Class<?> getClass(final ClassLoader classLoader, final String className, final boolean initialize) throws ClassNotFoundException {
        String next = className;
        int lastDotIndex;
        do {
            try {
                final Class<?> clazz = getPrimitiveClass(next);
                return clazz != null ? clazz : java.lang.Class.forName(toCanonicalName(next), initialize, classLoader);
            } catch (final ClassNotFoundException ex) {
                lastDotIndex = next.lastIndexOf(TiCharConst.DOT);
                if (lastDotIndex != -1) {
                    next = next.substring(0, lastDotIndex) + TiCharConst.DL + next.substring(lastDotIndex + 1);
                }
            }
        } while (lastDotIndex != -1);
        throw new ClassNotFoundException(next);
    }

    public static String getShortClassName(final Class<?> cls) {
        if (cls == null) {
            return TiStrConst.EMPTY;
        }
        return getShortClassName(cls.getName());
    }

    public static String getShortClassName(String className) {
        if (TiStrUtil.isEmpty(className)) {
            return TiStrConst.EMPTY;
        }
        final StringBuilder arrayPrefix = new StringBuilder();
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }

            if (REVERSE_ABBREVIATION_MAP.containsKey(className)) {
                className = REVERSE_ABBREVIATION_MAP.get(className);
            }
        }

        final int lastDotIdx = className.lastIndexOf(TiCharConst.DOT);
        final int innerIdx = className.indexOf(TiCharConst.DL, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace(TiCharConst.DL, TiCharConst.DOT);
        }
        return out + arrayPrefix;
    }

    private static Class<?> getPrimitiveClass(final String className) {
        return NAME_PRIMITIVE_MAP.get(className);
    }

    private static String toCanonicalName(final String className) {
        String canonicalName = TiStrUtil.deleteWhitespace(className);
        Objects.requireNonNull(canonicalName, "className");
        final String arrayMarker = "[]";
        if (canonicalName.endsWith(arrayMarker)) {
            final StringBuilder classNameBuffer = new StringBuilder();
            while (canonicalName.endsWith(arrayMarker)) {
                canonicalName = canonicalName.substring(0, canonicalName.length() - 2);
                classNameBuffer.append("[");
            }
            final String abbreviation = ABBREVIATION_MAP.get(canonicalName);
            if (abbreviation != null) {
                classNameBuffer.append(abbreviation);
            } else {
                classNameBuffer.append("L").append(canonicalName).append(";");
            }
            canonicalName = classNameBuffer.toString();
        }
        return canonicalName;
    }

}
