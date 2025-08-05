package top.ticho.tool.core;

import cn.hutool.core.collection.CollUtil;

import java.util.Collection;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:35
 */
public class TiCollUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return CollUtil.isEmpty(collection);
    }

}
