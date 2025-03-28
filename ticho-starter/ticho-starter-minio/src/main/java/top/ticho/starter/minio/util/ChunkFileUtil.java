package top.ticho.starter.minio.util;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

/**
 * 分片文件工具类
 *
 * @author zhajianjun
 * @date 2025-03-27 23:18
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Slf4j
public class ChunkFileUtil {

    /**
     * 大文件分割
     *
     * @param bigFile   大文件
     * @param chunkSize 分片大小;单位:Mb
     * @return {@link File } 分片文件夹
     */
    public static File fileSpliceChunk(File bigFile, long chunkSize) throws IOException {
        if (!bigFile.exists()) {
            throw new IOException(StrUtil.format("{}文件不存在", bigFile.getAbsolutePath()));
        }
        String chunkFolderFileName = FileNameUtil.mainName(bigFile.getAbsolutePath());
        // 本地大文件分片文件夹
        File localChunkFolder = new File(bigFile.getParentFile().getAbsolutePath() + File.separator + chunkFolderFileName);
        return fileSpliceChunk(bigFile, localChunkFolder, chunkSize);
    }

    /**
     * 文件拼接块
     *
     * @param bigFile          大文件
     * @param localChunkFolder 分片文件夹
     * @param chunkSize        分片大小;单位:Mb
     * @return {@link File }
     */
    public static File fileSpliceChunk(File bigFile, File localChunkFolder, long chunkSize) throws IOException {
        if (!localChunkFolder.exists()) {
            boolean mkdirs = localChunkFolder.mkdirs();
            log.info("分块文件夹{}不存在，创建文件夹结果{}", localChunkFolder.getAbsolutePath(), mkdirs);
        }
        // 分块大小 1MB
        long chunkSizeMb = 1024 * 1024 * chunkSize;
        // 分块数量
        long chunkNum = (long) Math.ceil(bigFile.length() * 1.0 / chunkSizeMb);
        log.info("分块总数：{}", chunkNum);
        // 缓冲区大小
        byte[] bytes = new byte[1024];
        // 使用RandomAccessFile访问文件
        @Cleanup RandomAccessFile raf_read = new RandomAccessFile(bigFile, "r");
        // 分块
        for (int i = 0; i < chunkNum; i++) {
            // 创建分块文件
            File chunkFile = new File(localChunkFolder.getAbsolutePath() + File.separator + i);
            if (chunkFile.exists()) {
                boolean delete = chunkFile.delete();
                log.info("索引为{}分块文件｛｝已存在，删除块文件结果{}", localChunkFolder.getAbsolutePath(), delete);
            }
            boolean newFile = chunkFile.createNewFile();
            if (newFile) {
                // 向分块文件中写数据
                @Cleanup RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
                int len;
                while ((len = raf_read.read(bytes)) != -1) {
                    raf_write.write(bytes, 0, len);
                    if (chunkFile.length() >= chunkSizeMb) {
                        break;
                    }
                }
                log.info("索引为{}分块文件{}已完成", i, localChunkFolder.getAbsolutePath());
            }
        }
        return localChunkFolder;
    }

    /**
     * 合并文件
     *
     * @param chunkFileFolderPath 分片文件夹路径
     * @param composefilePath     合并文件
     */
    public static void composeLocalObject(String chunkFileFolderPath, String composefilePath) throws IOException {
        composeLocalObject(new File(chunkFileFolderPath), new File(composefilePath));
    }

    /**
     * 合并文件
     *
     * @param chunkFileFolder 分片文件夹
     * @param composeFile     合并文件
     */
    public static void composeLocalObject(File chunkFileFolder, File composeFile) throws IOException {
        if (!chunkFileFolder.exists() || !chunkFileFolder.isDirectory()) {
            return;
        }
        File[] chunkFiles = chunkFileFolder.listFiles();
        if (Objects.isNull(chunkFiles) || chunkFiles.length == 0) {
            log.error("[{}]分片文件目录为空！", chunkFileFolder);
            return;
        }
        @Cleanup RandomAccessFile mergedFile = new RandomAccessFile(composeFile, "rw");
        if (mergedFile.length() > 0) {
            mergedFile.setLength(0);
        }
        // 缓冲区大小
        byte[] bytes = new byte[1024];
        for (File chunkFile : chunkFiles) {
            @Cleanup RandomAccessFile partFile = new RandomAccessFile(chunkFile, "r");
            int len;
            while ((len = partFile.read(bytes)) != -1) {
                mergedFile.write(bytes, 0, len);
            }
        }
        log.info("【{}】本地文件合并成功", composeFile);
    }

    public static void main(String[] args) throws IOException {
        String localFilePath = "D:\\cache\\temp\\test.zip";
        File localFile = new File(localFilePath);
        String mainName = FileNameUtil.mainName(localFile);
        String extName = FileNameUtil.extName(localFile);
        File localChunkFolder = fileSpliceChunk(localFile, 10);
        String newFileName = mainName + "-" + IdUtil.fastSimpleUUID() + StrUtil.DOT + extName;
        String newFilePath = localChunkFolder.getParent() + File.separator + newFileName;
        File newFile = new File(newFilePath);
        composeLocalObject(localChunkFolder, newFile);
    }

}
