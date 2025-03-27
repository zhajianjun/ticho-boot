package top.ticho.starter.minio.util;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

/**
 * 资源工具类
 *
 * @author zhajianjun
 * @date 2025-03-27 23:18
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Slf4j
public class StorageUtil {

    /**
     * 大文件分割
     *
     * @param file      本地大文件
     * @param chunkSize 分片大小;单位:Mb
     * @return {@link File } 分片文件夹
     * @throws IOException io异常
     */
    public static File fileSpliceChunk(File file, long chunkSize) throws IOException {
        if (!file.exists()) {
            log.info("文件{}不存在", file.getAbsolutePath());
            return null;
        }
        String chunkFolderFileName = FileNameUtil.mainName(file.getAbsolutePath());
        // 本地大文件分片文件夹
        File localChunkFolder = new File(file.getParentFile().getAbsolutePath() + File.separator + chunkFolderFileName);
        if (!localChunkFolder.exists()) {
            boolean mkdirs = localChunkFolder.mkdirs();
            log.info("分块文件夹{}不存在，创建文件夹结果{}", localChunkFolder.getAbsolutePath(), mkdirs);
        }
        // 分块大小 1MB
        long chunkSizeMb = 1024 * 1024 * chunkSize;
        // 分块数量
        long chunkNum = (long) Math.ceil(file.length() * 1.0 / chunkSizeMb);
        log.info("分块总数：{}", chunkNum);
        // 缓冲区大小
        byte[] bytes = new byte[1024];
        // 使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(file, "r");
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
                RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw");
                int len;
                while ((len = raf_read.read(bytes)) != -1) {
                    raf_write.write(bytes, 0, len);
                    if (chunkFile.length() >= chunkSizeMb) {
                        break;
                    }
                }
                raf_write.close();
                log.info("索引为{}分块文件{}已完成", i, localChunkFolder.getAbsolutePath());
            }
        }
        raf_read.close();
        return localChunkFolder;
    }

    /**
     * 合并文件到本地
     *
     * @param chunkFolderPath 分片所在文件夹路径
     * @param filePath        合并文件路径
     */
    public static void composeLocalObject(String chunkFolderPath, String filePath) throws IOException {
        composeLocalObject(new File(chunkFolderPath), new File(filePath));
    }

    public static void composeLocalObject(File chunkFolder, File filePath) throws IOException {
        if (!chunkFolder.exists() || !chunkFolder.isDirectory()) {
            return;
        }
        File[] chunkFiles = chunkFolder.listFiles();
        if (Objects.isNull(chunkFiles) || chunkFiles.length == 0) {
            log.error("[{}]分片文件目录为空！", chunkFolder);
            return;
        }
        RandomAccessFile mergedFile = new RandomAccessFile(filePath, "rw");
        if (mergedFile.length() > 0) {
            mergedFile.setLength(0);
        }
        // 缓冲区大小
        byte[] bytes = new byte[1024];
        for (File chunkFile : chunkFiles) {
            RandomAccessFile partFile = new RandomAccessFile(chunkFile, "r");
            int len;
            while ((len = partFile.read(bytes)) != -1) {
                mergedFile.write(bytes, 0, len);
            }
            partFile.close();
        }
        mergedFile.close();
        log.info("【{}】本地文件合并成功", filePath);
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
