package top.ticho.tool.core;

import cn.hutool.core.util.URLUtil;

import java.net.URI;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-10 10:48
 */
public class TiUrlUtil {

    public static URI toURI(String location) {
        return URLUtil.toURI(location);
    }

    public static String encodeAll(String url) {
        return URLUtil.encodeAll(url);
    }

}
