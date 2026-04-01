package top.ticho.starter.view.log;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志配置
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40
 */
@Data
public class TiLogProperty {

    /** 是否开启日志拦截器 */
    private Boolean enable = true;
    /** 是否打印日志 */
    private Boolean print = false;
    /** 日志打印前缀 */
    private String reqPrefix = "[REQ]";
    /** 日志过滤地址 */
    private List<String> antPatterns = new ArrayList<>();
    /** 拦截器排序 */
    private Integer order = Integer.MIN_VALUE + 10000;
    /**
     * 敏感字段名称列表（支持模糊匹配），用于日志脱敏
     * 默认包含：password, pwd, passwd, secret, token, authorization, phone, mobile, idCard, idcard
     * 配置示例：ticho.log.sensitive-fields=password,secret,token,authorization
     */
    private String sensitiveFields;

}
