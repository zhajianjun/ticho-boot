package top.ticho.intranet.common.exception;

/**
 * 自定义异常
 *
 * @author zhajianjun
 * @date 2025-05-25 18:34
 */
public class IntranetException extends RuntimeException {

    public IntranetException(String message) {
        super(message);
    }

    public IntranetException(String message, Exception e) {
        super(message, e);
    }

}
