package top.ticho.tool.core;

import top.ticho.tool.core.exception.TiUtilException;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * IO 工具类
 * <p>提供常用的输入输出操作方法，包括流拷贝、读写转换、文件操作等</p>
 *
 * @author zhajianjun
 * @date 2025-08-17 14:20
 */
public class TiIoUtil {
    /** 文件结束标志 */
    public static final int EOF = -1;

    /** 默认缓冲区大小：8KB */
    public static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 使用默认缓冲区大小拷贝输入流到输出流
     *
     * @param in  输入流
     * @param out 输出流
     * @return 拷贝的字节数
     */
    public static long copy(InputStream in, OutputStream out) {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝输入流到输出流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓冲区大小
     * @return 拷贝的字节数
     */
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
            throw new TiUtilException(e);
        }
    }

    /**
     * 读取输入流的所有字节
     *
     * @param in 输入流
     * @return 字节数组
     */
    public static byte[] readBytes(InputStream in) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            copy(in, out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 读取输入流内容为字符串（指定字符集）
     *
     * @param in      输入流
     * @param charset 字符集
     * @return 字符串
     */
    public static String readString(InputStream in, Charset charset) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toString(charset);
    }

    /**
     * 读取输入流内容为字符串（使用 UTF-8 编码）
     *
     * @param in 输入流
     * @return 字符串
     */
    public static String readString(InputStream in) {
        return readString(in, StandardCharsets.UTF_8);
    }

    /**
     * 写入字节数组到输出流
     *
     * @param out  输出流
     * @param data 字节数组
     */
    public static void writeBytes(OutputStream out, byte[] data) {
        try {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 写入字符串到输出流（指定字符集）
     *
     * @param out     输出流
     * @param data    字符串
     * @param charset 字符集
     */
    public static void writeString(OutputStream out, String data, Charset charset) {
        try {
            out.write(data.getBytes(charset));
            out.flush();
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 写入字符串到输出流（使用 UTF-8 编码）
     *
     * @param out  输出流
     * @param data 字符串
     */
    public static void writeString(OutputStream out, String data) {
        writeString(out, data, StandardCharsets.UTF_8);
    }

    /**
     * 安静地关闭 AutoCloseable 对象（不抛出异常）
     *
     * @param closeable 需要关闭的对象
     */
    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 安静地关闭 Closeable 对象（不抛出异常）
     *
     * @param closeable 需要关闭的对象
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * 读取文件的所有字节（Path 方式）
     *
     * @param path 文件路径
     * @return 字节数组
     */
    public static byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 读取文件的所有字节（字符串路径方式）
     *
     * @param filePath 文件路径
     * @return 字节数组
     */
    public static byte[] readAllBytes(String filePath) {
        return readAllBytes(Paths.get(filePath));
    }

    /**
     * 写字节数组到文件（Path 方式）
     *
     * @param path 文件路径
     * @param data 字节数组
     */
    public static void writeBytes(Path path, byte[] data) {
        try {
            Files.write(path, data);
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 写字节数组到文件（字符串路径方式）
     *
     * @param filePath 文件路径
     * @param data     字节数组
     */
    public static void writeBytes(String filePath, byte[] data) {
        writeBytes(Paths.get(filePath), data);
    }

    /**
     * 读取文件内容为字符串（Path 方式，指定字符集）
     *
     * @param path    文件路径
     * @param charset 字符集
     * @return 文件内容字符串
     */
    public static String readFileToString(Path path, Charset charset) {
        try {
            return Files.readString(path, charset);
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 读取文件内容为字符串（字符串路径方式，指定字符集）
     *
     * @param filePath 文件路径
     * @param charset  字符集
     * @return 文件内容字符串
     */
    public static String readFileToString(String filePath, Charset charset) {
        return readFileToString(Paths.get(filePath), charset);
    }

    /**
     * 读取文件内容为字符串（Path 方式，UTF-8 编码）
     *
     * @param path 文件路径
     * @return 文件内容字符串
     */
    public static String readFileToString(Path path) {
        return readFileToString(path, StandardCharsets.UTF_8);
    }

    /**
     * 读取文件内容为字符串（字符串路径方式，UTF-8 编码）
     *
     * @param filePath 文件路径
     * @return 文件内容字符串
     */
    public static String readFileToString(String filePath) {
        return readFileToString(Paths.get(filePath), StandardCharsets.UTF_8);
    }

    /**
     * 写字符串到文件（Path 方式，指定字符集）
     *
     * @param path    文件路径
     * @param data    字符串数据
     * @param charset 字符集
     */
    public static void writeStringToFile(Path path, String data, Charset charset) {
        try {
            Files.writeString(path, data, charset);
        } catch (IOException e) {
            throw new TiUtilException(e);
        }
    }

    /**
     * 写字符串到文件（字符串路径方式，指定字符集）
     *
     * @param filePath 文件路径
     * @param data     字符串数据
     * @param charset  字符集
     */
    public static void writeStringToFile(String filePath, String data, Charset charset) {
        writeStringToFile(Paths.get(filePath), data, charset);
    }

    /**
     * 写字符串到文件（Path 方式，UTF-8 编码）
     *
     * @param path 文件路径
     * @param data 字符串数据
     */
    public static void writeStringToFile(Path path, String data) {
        writeStringToFile(path, data, StandardCharsets.UTF_8);
    }

    /**
     * 写字符串到文件（字符串路径方式，UTF-8 编码）
     *
     * @param filePath 文件路径
     * @param data     字符串数据
     */
    public static void writeStringToFile(String filePath, String data) {
        writeStringToFile(Paths.get(filePath), data, StandardCharsets.UTF_8);
    }

}
