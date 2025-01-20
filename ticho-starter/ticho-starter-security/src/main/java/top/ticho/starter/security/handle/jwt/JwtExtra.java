package top.ticho.starter.security.handle.jwt;

import java.util.Map;

/**
 * jwt扩展信息接口
 * <p>通过实现接口可在token中添加自定义信息</p>
 *
 * @author zhajianjun
 * @date 2022-09-23 10:45
 */
public interface JwtExtra {

    /**
     * 获取扩展信息Map
     *
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> getExtra();

}
