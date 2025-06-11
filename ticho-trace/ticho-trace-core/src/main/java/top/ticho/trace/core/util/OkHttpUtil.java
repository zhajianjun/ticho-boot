package top.ticho.trace.core.util;

import top.ticho.tool.json.util.TiJsonUtil;

/**
 * 使用okhttp进行适配
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class OkHttpUtil {
    /** http客户端 */

    public static void push(String url, String secret, Object data) {
        System.out.println(TiJsonUtil.toJsonString(data));
    }


}
