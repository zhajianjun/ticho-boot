package top.ticho.tool.core;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * @author zhajianjun
 * @date 2025-08-04 22:41
 */
public class TiIdUtil {
    private static final Snowflake snowflake = IdUtil.getSnowflake(0, 0);

    public static Long getId() {
        return snowflake.nextId();
    }

    public static String getIdStr() {
        return snowflake.nextIdStr();
    }

    public static String getSimpleUuid() {
        return IdUtil.fastSimpleUUID();
    }

    public static String getUuid() {
        return IdUtil.fastUUID();
    }

}
