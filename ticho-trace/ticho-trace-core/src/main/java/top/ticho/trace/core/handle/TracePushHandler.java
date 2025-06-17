package top.ticho.trace.core.handle;

/**
 * 使用okhttp进行适配
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public interface TracePushHandler {
    /** http客户端 */

    void push(Object data);


}
