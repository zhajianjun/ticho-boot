package top.ticho.tool.core;

import top.ticho.tool.core.exception.TiUtilException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * URL 工具类
 * <p>提供 URI 转换和 URL 编码等常用操作</p>
 *
 * @author zhajianjun
 * @date 2025-08-10 10:48
 */
public class TiUrlUtil {

    /**
     * 将字符串转换为 URI 对象
     *
     * @param location 待转换的字符串，会自动去除首尾空白字符
     * @return 转换后的 URI 对象
     * @throws TiUtilException 当 URI 格式不正确时抛出此异常
     */
    public static URI toURI(String location) {
        try {
            return new URI(TiStrUtil.trim(location));
        } catch (URISyntaxException e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 对 URL 进行 UTF-8 编码
     *
     * @param url 待编码的 URL 字符串
     * @return 编码后的字符串，如果输入为 null 则返回 null
     */
    public static String encode(String url) {
        if (Objects.isNull(url)) {
            return null;
        }
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }

}
