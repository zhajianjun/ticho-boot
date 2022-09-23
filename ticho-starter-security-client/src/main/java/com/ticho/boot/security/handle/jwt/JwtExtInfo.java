package com.ticho.boot.security.handle.jwt;

import java.util.Map;

/**
 * jwt扩展接口
 * <p>通过实现接口可在token中添加自定义信息</p>
 *
 * @author zhajianjun
 * @date 2022-09-23 10:45
 */
public interface JwtExtInfo {

    Map<String, Object> getExt();

}
