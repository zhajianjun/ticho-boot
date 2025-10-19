package top.ticho.tool.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import top.ticho.tool.core.exception.TiSysException;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 *
 * @author zhajianjun
 * @date 2025-08-12 01:33
 */
public class TiFileUtil {

    public static String getMimeType(String filePath) {
        if (TiStrUtil.isBlank(filePath)) {
            return null;
        }
        if (TiStrUtil.endWith(filePath, ".css", true, false)) {
            return "text/css";
        } else if (TiStrUtil.endWith(filePath, ".js", true, false)) {
            return "application/x-javascript";
        } else if (TiStrUtil.endWith(filePath, ".rar", true, false)) {
            return "application/x-rar-compressed";
        } else if (TiStrUtil.endWith(filePath, ".7z", true, false)) {
            return "application/x-7z-compressed";
        } else if (TiStrUtil.endWith(filePath, ".wgt", true, false)) {
            return "application/widget";
        } else if (TiStrUtil.endWith(filePath, ".webp", true, false)) {
            // JDK8不支持
            return "image/webp";
        }
        String contentType = URLConnection.getFileNameMap().getContentTypeFor(filePath);
        if (null == contentType) {
            Path path = Paths.get(filePath);
            try {
                return Files.probeContentType(path);
            } catch (IOException ignore) {
                return null;
            }
        }
        return contentType;
    }

    public static String getName(String fileName) {
        return FilenameUtils.getName(fileName);
    }

    public static String mainName(String fileName) {
        return FilenameUtils.getBaseName(fileName);
    }

    public static String extName(String name) {
        return FilenameUtils.getExtension(name);
    }

    public static boolean del(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            throw new TiSysException(e);
        }
        return true;
    }

    public static boolean exist(File file) {
        return (null != file) && file.exists();
    }

    public static File mkdir(File dir) {
        try {
            FileUtils.forceMkdir(dir);
        } catch (IOException e) {
            throw new TiSysException(e);
        }
        return dir;
    }

    public static void writeBytes(byte[] data, File dest) {
        try {
            FileUtils.writeByteArrayToFile(dest, data);
        } catch (IOException e) {
            throw new TiSysException(e);
        }
    }

    public static void writeBytes(byte[] data, String path) {
        try {
            File dest = new File(path);
            FileUtils.touch(dest);
            FileUtils.writeByteArrayToFile(dest, data);
        } catch (IOException e) {
            throw new TiSysException(e);
        }
    }

    public static void moveFile(File src, File target) {
        try {
            FileUtils.moveFile(src, target);
        } catch (IOException e) {
            throw new TiSysException(e);
        }
    }

}
