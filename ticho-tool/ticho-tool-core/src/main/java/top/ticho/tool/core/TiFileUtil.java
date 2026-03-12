package top.ticho.tool.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import top.ticho.tool.core.exception.TiUtilException;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件工具类
 * 提供文件相关的常用操作，如 MIME 类型获取、文件名处理、文件读写等
 *
 * @author zhajianjun
 * @date 2025-08-12 01:33
 */
public class TiFileUtil {

    /**
     * 获取文件的 MIME 类型
     * 先通过文件扩展名判断常见类型，再使用 URLConnection 和 Files.probeContentType 获取
     *
     * @param filePath 文件路径
     * @return MIME 类型字符串，如果无法识别则返回 null
     */
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
            // JDK8 不支持
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

    /**
     * 获取文件名（包含扩展名）
     * 从完整路径中提取文件名部分
     *
     * @param fileName 文件路径或文件名
     * @return 文件名（包含扩展名）
     */
    public static String getName(String fileName) {
        return FilenameUtils.getName(fileName);
    }

    /**
     * 获取主文件名（不包含扩展名）
     * 从文件名中去除扩展名部分
     *
     * @param fileName 文件路径或文件名
     * @return 主文件名（不包含扩展名）
     */
    public static String mainName(String fileName) {
        return FilenameUtils.getBaseName(fileName);
    }

    /**
     * 获取文件扩展名
     * 提取文件名中的扩展名部分（不包含点号）
     *
     * @param name 文件名
     * @return 文件扩展名（不包含点号），如果没有扩展名则返回空字符串
     */
    public static String extName(String name) {
        return FilenameUtils.getExtension(name);
    }

    /**
     * 删除文件
     *
     * @param file 要删除的文件对象
     * @return 删除成功返回 true，失败则抛出异常
     * @throws TiUtilException 删除失败时抛出
     */
    public static boolean del(File file) {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
        return true;
    }

    /**
     * 判断文件是否存在
     *
     * @param file 要检查的文件对象
     * @return 文件存在且不为 null 时返回 true，否则返回 false
     */
    public static boolean exist(File file) {
        return (null != file) && file.exists();
    }

    /**
     * 创建目录
     * 如果目录已存在则不执行任何操作，如果父目录不存在会一并创建
     *
     * @param dir 要创建的目录对象
     * @return 创建后的目录对象
     * @throws TiUtilException 创建失败时抛出
     */
    public static File mkdir(File dir) {
        try {
            FileUtils.forceMkdir(dir);
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
        return dir;
    }

    /**
     * 写入字节数组到文件
     *
     * @param data 要写入的字节数组
     * @param dest 目标文件对象
     * @throws TiUtilException 写入失败时抛出
     */
    public static void writeBytes(byte[] data, File dest) {
        try {
            FileUtils.writeByteArrayToFile(dest, data);
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 写入字节数组到文件
     *
     * @param data 要写入的字节数组
     * @param path 目标文件路径
     * @throws TiUtilException 写入失败时抛出
     */
    public static void writeBytes(byte[] data, String path) {
        try {
            File dest = new File(path);
            FileUtils.touch(dest);
            FileUtils.writeByteArrayToFile(dest, data);
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 移动文件
     * 将源文件移动到目标位置，如果目标文件已存在会被覆盖
     *
     * @param src 源文件对象
     * @param target 目标文件对象
     * @throws TiUtilException 移动失败时抛出
     */
    public static void moveFile(File src, File target) {
        try {
            FileUtils.moveFile(src, target);
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

}
