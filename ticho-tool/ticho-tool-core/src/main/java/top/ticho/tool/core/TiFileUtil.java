package top.ticho.tool.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-12 01:33
 */
public class TiFileUtil {

    public static String getMimeType(String filePath) {
        return FileUtil.getMimeType(filePath);
    }

    public static String getName(String fileName) {
        return FileNameUtil.getName(fileName);
    }

    public static String mainName(String fileName) {
        return FileNameUtil.mainName(fileName);
    }

    public static String extName(String name) {
        return FileNameUtil.extName(name);
    }

}
