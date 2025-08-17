package top.ticho.tool.core;

import cn.hutool.core.io.IoUtil;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-17 14:20
 */
public class TiIoUtil {

    public static long copy(InputStream in, OutputStream out, int bufferSize) {
        return IoUtil.copy(in, out, bufferSize);
    }

}
