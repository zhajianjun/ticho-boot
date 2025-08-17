package top.ticho.tool.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;

import java.io.File;

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

    public static boolean del(File file) {
        return FileUtil.del(file);
    }

    public static boolean exist(File file) {
        return FileUtil.exist(file);
    }

    public static File mkdir(File dir) {
        return FileUtil.mkdir(dir);
    }

    public static File writeBytes(byte[] data, File dest) {
        return FileUtil.writeBytes(data, dest);
    }

    public static File writeBytes(byte[] data, String path) {
        return FileUtil.writeBytes(data, path);
    }

    public static void moveContent(File src, File target, boolean isOverride) {
        FileUtil.moveContent(src, target, isOverride);
    }

}
