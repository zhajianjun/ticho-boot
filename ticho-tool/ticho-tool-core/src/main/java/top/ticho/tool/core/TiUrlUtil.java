package top.ticho.tool.core;

import top.ticho.tool.core.exception.TiSysException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-10 10:48
 */
public class TiUrlUtil {

    public static URI toURI(String location) {
        try {
            return new URI(TiStrUtil.trim(location));
        } catch (URISyntaxException e) {
            throw new TiSysException(e);
        }
    }

    public static String encode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

}
