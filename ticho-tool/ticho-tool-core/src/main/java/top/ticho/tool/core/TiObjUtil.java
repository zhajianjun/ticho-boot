package top.ticho.tool.core;

import cn.hutool.core.util.ObjUtil;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-08 23:21
 */
public class TiObjUtil {

    public static boolean isEmpty(Object obj) {
        return ObjUtil.isEmpty(obj);
    }

    public static boolean isNotEmpty(Object obj) {
        return ObjUtil.isNotEmpty(obj);
    }

    public static int length(Object obj) {
        return ObjUtil.length(obj);
    }

}
