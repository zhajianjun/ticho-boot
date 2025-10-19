package top.ticho.tool.core;

import top.ticho.tool.core.exception.TiSysException;

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
    public static final int EOF = -1;

    public static long copy(InputStream in, OutputStream out, int bufferSize) {
        try {
            long count = 0;
            int n;
            byte[] buffer = new byte[bufferSize];
            while (EOF != (n = in.read(buffer))) {
                out.write(buffer, 0, n);
                count += n;
            }
            return count;
        } catch (IOException e) {
            throw new TiSysException(e);
        }
    }

}
