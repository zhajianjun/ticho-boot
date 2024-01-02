package top.ticho.boot.security.annotation;

/**
 * 忽略检查类型
 *
 * @author zhajianjun
 * @date 2023-04-17 10:59
 */
public enum IgnoreType {

    /** 全部忽略检查 */
    ALL,
    /** 内网接口忽略检查，判断依据是header中是否有inner参数为true */
    INNER;

}
