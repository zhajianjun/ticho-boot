package top.ticho.tool.core.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

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
        super(ExceptionUtil.getMessage(e), e);
    }

    public TiUtilException(String message) {
        super(message);
    }

    public TiUtilException(String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params));
    }

    public TiUtilException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TiUtilException(Throwable throwable, String messageTemplate, Object... params) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }

    public boolean causeInstanceOf(Class<? extends Throwable> clazz) {
        final Throwable cause = this.getCause();
        return null != clazz && clazz.isInstance(cause);
    }

}
