package top.ticho.tool.core;

import cn.hutool.core.util.StrUtil;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:32
 */
public class TiStrUtil {

    public static boolean isBlankIfStr(Object obj) {
        return StrUtil.isBlankIfStr(obj);
    }

    public static boolean isBlank(CharSequence str) {
        return StrUtil.isBlank(str);
    }

    public static boolean isNotBlank(CharSequence str) {
        return StrUtil.isNotBlank(str);
    }

}
