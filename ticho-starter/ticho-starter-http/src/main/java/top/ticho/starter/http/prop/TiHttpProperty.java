package top.ticho.starter.http.prop;

import lombok.Data;

/**
 * http参数
 *
 * @author zhajianjun
 * @date 2022-11-01 13:12
 */
@Data
public class TiHttpProperty {

    /** 开启ticho http，默认使用okhttp */
    private Boolean enable = true;
    /** 开启日志拦截 */
    private Boolean openLog = true;
    /* 日志拦截是否打印日志 */
    private Boolean printLog = false;

}
