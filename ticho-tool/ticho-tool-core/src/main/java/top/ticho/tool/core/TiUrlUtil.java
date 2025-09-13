package top.ticho.tool.core;

import org.apache.commons.lang3.StringUtils;
import top.ticho.tool.core.exception.TiUtilException;

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
            return new URI(StringUtils.trim(location));
        } catch (URISyntaxException e) {
            throw new TiUtilException(e);
        }
    }

    public static String encode(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

    /**
     * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
     *
     * @return unreserved字符
     */
    private static StringBuilder unreservedChars() {
        StringBuilder sb = new StringBuilder();

        // ALPHA
        for (char c = 'A'; c <= 'Z'; c++) {
            sb.append(c);
        }
        for (char c = 'a'; c <= 'z'; c++) {
            sb.append(c);
        }

        // DIGIT
        for (char c = '0'; c <= '9'; c++) {
            sb.append(c);
        }

        // "-" / "." / "_" / "~"
        sb.append("_.-~");

        return sb;
    }

}
