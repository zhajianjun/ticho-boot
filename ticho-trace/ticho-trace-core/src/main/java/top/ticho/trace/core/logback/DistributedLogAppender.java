package top.ticho.trace.core.logback;

import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.slf4j.helpers.MessageFormatter;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.core.handle.LogHandleContext;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;

/**
 * 分布式日志appender
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class DistributedLogAppender extends AbstractAppender {

    /** 应用名称 */
    @Setter
    private String appName = LogConst.UNKNOWN;
    /** 应用环境 */
    @Setter
    private String env = LogConst.UNKNOWN;
    /** 日志推送的url */
    @Setter
    private String url;
    /** 日志推送的秘钥 */
    @Setter
    private String secret;
    /** 批量推送的日志数量，达到一定数量则进行推送日志 */
    @Setter
    private int pushSize = 100;
    /** 日志推送的时间间隔，单位：毫秒（ms） */
    @Setter
    private int flushInterval = 500;
    /** 是否推送日志 */
    @Setter
    private Boolean pushLog = false;
    /** 日志处理上下文 */
    private LogHandleContext logHandleContext;

    public DistributedLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }


    @Override
    public void start() {
        super.start();
        if (!pushLog) {
            return;
        }
        this.logHandleContext = new LogHandleContext(appName, env, url, secret, pushSize, flushInterval);
        logHandleContext.start();
    }

    @Override
    public void stop() {
        super.stop();
        if (!pushLog) {
            return;
        }
        logHandleContext.stop();
    }

    @Override
    public void append(LogEvent event) {
        if (!pushLog) {
            return;
        }
        if (event == null) {
            return;
        }
        logHandleContext.publish(logInfo -> {
            Map<String, String> mdcMap = event.getContextData().toMap();
            logInfo.setLogLevel(event.getLevel().toString());
            logInfo.setDtTime(event.getTimeMillis());
            logInfo.setClassName(event.getLoggerName());
            logInfo.setContent(getMessage(event));
            logInfo.setThreadName(event.getThreadName());
            logInfo.setMdc(mdcMap);
            String method = "";
            StackTraceElement[] stackTraceElements = event.getThrown().getStackTrace();
            if (stackTraceElements != null && stackTraceElements.length > 0) {
                StackTraceElement stackTraceElement = stackTraceElements[0];
                method = stackTraceElement.getMethodName();
                String line = String.valueOf(stackTraceElement.getLineNumber());
                method = method + "(" + stackTraceElement.getFileName() + ":" + line + ")";
            }
            logInfo.setMethod(method);
        });
    }


    private static String getMessage(LogEvent logEvent) {
        String message = logEvent.getMessage().getFormattedMessage();
        if (!logEvent.getLevel().equals(Level.ERROR)) {
            return message;
        }
        if (logEvent.getThrownProxy() != null) {
            ThrowableProxy throwableProxy = logEvent.getThrownProxy();
            String arg = message + "\n" + errorStackTrace(throwableProxy.getThrowable()).toString();
            return packageMessage("{}", arg);
        }
        Object[] args = logEvent.getMessage().getParameters();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Throwable) {
                    args[i] = errorStackTrace(args[i]);
                }
            }
            return packageMessage(message, args);
        }
        return message;
    }

    private static String packageMessage(String message, Object... args) {
        if (message != null && message.contains("{}")) {
            return MessageFormatter.arrayFormat(message, args).getMessage();
        }
        StringBuilder builder = new StringBuilder(128);
        builder.append(message);
        for (Object arg : args) {
            builder.append("\n").append(arg);
        }
        return builder.toString();
    }

    public static Object errorStackTrace(Object obj) {
        if (obj instanceof Exception) {
            Exception eObj = (Exception) obj;
            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                String exceptionStack;
                eObj.printStackTrace(pw);
                exceptionStack = sw.toString();
                return exceptionStack;
            } catch (Exception e) {
                e.printStackTrace();
                return obj;
            }
        }
        return obj;
    }

}