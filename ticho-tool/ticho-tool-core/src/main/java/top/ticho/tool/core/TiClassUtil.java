package top.ticho.tool.core;

import cn.hutool.core.util.ClassUtil;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-09 20:07
 */
public class TiClassUtil {

    public static boolean isSimpleValueType(Class<?> aClass) {
        return ClassUtil.isSimpleValueType(aClass);
    }

}
