package top.ticho.tool.core;

import org.apache.commons.io.IOUtils;
import top.ticho.tool.core.exception.TiUtilException;

import java.io.IOException;
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
        try {
            return IOUtils.copy(in, out, bufferSize);
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

}
