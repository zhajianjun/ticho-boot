package top.ticho.tool.core.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serial;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-17 16:15
 */
public class TiUtilException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8247610319171014183L;

    public TiUtilException(Throwable e) {
        super(ExceptionUtils.getMessage(e), e);
    }

    public TiUtilException(String message) {
        super(message);
    }

    public TiUtilException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public boolean causeInstanceOf(Class<? extends Throwable> clazz) {
        final Throwable cause = this.getCause();
        return null != clazz && clazz.isInstance(cause);
    }

}
