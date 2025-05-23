package top.ticho.starter.view.enums;

/**
 * 错误码接口
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
public interface TiErrCode {

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
    String getMsg();

}
