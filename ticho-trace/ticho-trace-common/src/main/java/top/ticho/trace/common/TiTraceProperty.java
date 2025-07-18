package top.ticho.trace.common;

import lombok.Data;

/**
 * 链路配置
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Data
public class TiTraceProperty {

    /** 链路表达式 */
    private String trace = TiTraceConst.DEFAULT_TRACE;
    /** 拦截器排序 */
    private Integer order = Integer.MIN_VALUE + 1000;

}
