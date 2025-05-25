package top.ticho.intranet.server.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务状态
 *
 * @author zhajianjun
 * @date 2025-05-18 11:11
 */
@Getter
@AllArgsConstructor
public enum ServerStatus {

    INITING(-1, "初始化中"),
    STARTING(0, "启动中"),
    ENABLED(1, "已启用"),
    DISABLED(2, "已禁用"),
    ;
    private final int code;
    private final String msg;

}
