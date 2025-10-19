package top.ticho.tool.core.enums;

/**
 * 错误码接口
 *
 * @author zhajianjun
 * @date 2025-10-19 13:07
 */
public interface TiErrorCode {

    /**
     * 错误码
     *
     * @return int
     */
    int getCode();


    /**
     * 错误信息
     *
     * @return {@link String }
     */
    String getMessage();

}
