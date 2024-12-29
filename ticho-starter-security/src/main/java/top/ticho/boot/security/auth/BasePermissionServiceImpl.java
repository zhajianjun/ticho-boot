package top.ticho.boot.security.auth;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import top.ticho.tool.json.util.TiJsonUtil;

/**
 * 接口权限实现
 *
 * @author zhajianjun
 * @date 2022-09-26 17:31:58
 */
@Slf4j
public class BasePermissionServiceImpl implements PermissionService {

    public boolean hasPerms(String... permissions) {
        if (ArrayUtil.isEmpty(permissions)) {
            return false;
        }
        log.info("权限校验，permissions = {}", String.join(",", permissions));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        log.info("权限用户信息，user = {}", TiJsonUtil.toJsonString(authentication.getPrincipal()));
        return true;
    }

}
