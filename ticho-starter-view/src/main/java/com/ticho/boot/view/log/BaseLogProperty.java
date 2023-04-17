package com.ticho.boot.view.log;


import lombok.Data;

/**
 * 日志配置
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@Data
public class BaseLogProperty {

    /** 是否开启日志拦截器 */
    private Boolean enable = true;
    /** 是否打印日志 */
    private Boolean print = false;
    /** 日志打印前缀 */
    private String reqPrefix = "[REQ]";
    /** 拦截器排序 */
    private Integer order = Integer.MIN_VALUE + 10000;

}
