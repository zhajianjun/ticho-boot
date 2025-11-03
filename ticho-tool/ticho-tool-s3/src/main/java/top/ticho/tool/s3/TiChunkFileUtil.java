package top.ticho.tool.s3;

import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ticho.tool.core.TiFileUtil;
import top.ticho.tool.core.TiIdUtil;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.core.constant.TiStrConst;
import top.ticho.tool.core.unit.DataSize;
import top.ticho.tool.core.unit.DataUnit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 分片文件工具类
 *
 * @author zhajianjun
 * @date 2025-10-29 19:27
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Slf4j
public class TiChunkFileUtil {

    /**
     * 大文件分割
     *
     * @param bigFile   大文件
     * @param chunkSize 分片大小;单位:Mb
     * @return {@link File } 分片文件夹
     * @throws IOException io异常
     */
    public static File fileSpliceChunk(File bigFile, long chunkSize) throws IOException {
        return fileSpliceChunk(bigFile, chunkSize, DataUnit.MEGABYTES);
    }

    /**
     * 大文件分割
     *
     * @param bigFile   大文件
     * @param chunkSize 分片大小;单位:Mb
     * @return {@link File } 分片文件夹
     */
    public static File fileSpliceChunkAsync(File bigFile, long chunkSize, Executor executor) {
        return fileSpliceChunkAsync(bigFile, chunkSize, DataUnit.MEGABYTES, executor);
    }

    /**
     * 大文件分割
     *
     * @param bigFile   大文件
     * @param chunkSize 分片大小
     * @param dataUnit  数据单位;缺省为MB
     * @return {@link File } 分片文件夹
     * @throws IOException io异常
     */
    public static File fileSpliceChunk(File bigFile, long chunkSize, DataUnit dataUnit) throws IOException {
        if (!bigFile.exists()) {
            throw new RuntimeException(TiStrUtil.format("{}文件不存在", bigFile.getAbsolutePath()));
        }
        String chunkFolderFileName = TiFileUtil.mainName(bigFile.getAbsolutePath());
        // 本地大文件分片文件夹
        File localChunkFolder = new File(bigFile.getParentFile().getAbsolutePath() + File.separator + chunkFolderFileName);
        return fileSpliceChunk(bigFile, localChunkFolder, chunkSize, dataUnit);
    }

    /**
     * 大文件分割
     *
     * @param bigFile   大文件
     * @param chunkSize 分片大小
     * @param dataUnit  数据单位;缺省为MB
     * @return {@link File } 分片文件夹
     */
    public static File fileSpliceChunkAsync(File bigFile, long chunkSize, DataUnit dataUnit, Executor executor) {
        if (!bigFile.exists()) {
            throw new RuntimeException(TiStrUtil.format("{}文件不存在", bigFile.getAbsolutePath()));
        }
        String chunkFolderFileName = TiFileUtil.mainName(bigFile.getAbsolutePath());
        // 本地大文件分片文件夹
        File localChunkFolder = new File(bigFile.getParentFile().getAbsolutePath() + File.separator + chunkFolderFileName);
        return fileSpliceChunkAsync(bigFile, localChunkFolder, chunkSize, dataUnit, executor);
    }

    /**
     * 文件拼接块
     *
     * @param bigFile          大文件
     * @param localChunkFolder 分片文件夹
     * @param chunkSize        分片大小;单位:Mb
     * @return {@link File }
     * @throws IOException io异常
     */
    public static File fileSpliceChunk(File bigFile, File localChunkFolder, long chunkSize) throws IOException {
        return fileSpliceChunk(bigFile, localChunkFolder, chunkSize, DataUnit.MEGABYTES);
    }

    /**
     * 文件拼接块
     *
     * @param bigFile          大文件
     * @param localChunkFolder 分片文件夹
     * @param chunkSize        分片大小
     * @param dataUnit         数据单位;缺省为MB
     * @return {@link File }
     * @throws IOException io异常
     */
    public static File fileSpliceChunk(File bigFile, File localChunkFolder, long chunkSize, DataUnit dataUnit) throws IOException {
        mkdirs(localChunkFolder);
        DataUnit unit = Objects.isNull(dataUnit) ? DataUnit.MEGABYTES : dataUnit;
        DataSize dataSize = DataSize.of(chunkSize, unit);
        long chunkSizeBytes = dataSize.toBytes();
        long chunkNum = (long) Math.ceil(bigFile.length() * 1.0 / chunkSizeBytes);
        log.info("分块总数：{}", chunkNum);
        for (int i = 0; i < chunkNum; i++) {
            createChunkFile(bigFile, localChunkFolder, i, chunkSizeBytes);
        }
        // 等待所有任务完成
        return localChunkFolder;
    }

    public static File fileSpliceChunkAsync(File bigFile, File localChunkFolder, long chunkSize, DataUnit dataUnit, Executor executor) {
        mkdirs(localChunkFolder);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        DataUnit unit = Objects.isNull(dataUnit) ? DataUnit.MEGABYTES : dataUnit;
        DataSize dataSize = DataSize.of(chunkSize, unit);
        long chunkSizeBytes = dataSize.toBytes();
        long chunkNum = (long) Math.ceil(bigFile.length() * 1.0 / chunkSizeBytes);
        for (int i = 0; i < chunkNum; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    createChunkFile(bigFile, localChunkFolder, index, chunkSizeBytes);
                } catch (IOException e) {
                    log.error("创建分片文件失败", e);
                }
            }, executor);
            futures.add(future);
        }
        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        return localChunkFolder;
    }

    private static void mkdirs(File localChunkFolder) {
        if (!localChunkFolder.exists()) {
            boolean mkdirs = localChunkFolder.mkdirs();
            log.info("文件夹{}不存在，创建文件夹结果{}", localChunkFolder.getAbsolutePath(), mkdirs);
        }
    }

    private static void createChunkFile(File bigFile, File localChunkFolder, int index, long chunkSizeBytes) throws IOException {
        // 创建分块文件
        File chunkFile = new File(localChunkFolder.getAbsolutePath() + File.separator + index);
        if (chunkFile.exists()) {
            boolean delete = chunkFile.delete();
            log.info("索引为{}分块文件已存在，删除块文件结果{}", index, delete);
        }
        boolean newFile = chunkFile.createNewFile();
        if (newFile) {
            // 计算分片起始位置
            long startPos = index * chunkSizeBytes;
            long endPos = Math.min(startPos + chunkSizeBytes, bigFile.length());
            // 使用RandomAccessFile随机访问指定位置
            try (RandomAccessFile rafRead = new RandomAccessFile(bigFile, "r");
                 RandomAccessFile rafWrite = new RandomAccessFile(chunkFile, "rw")) {
                rafRead.seek(startPos);
                byte[] buffer = new byte[8192];
                long remaining = endPos - startPos;
                while (remaining > 0) {
                    int readSize = (int) Math.min(buffer.length, remaining);
                    int len = rafRead.read(buffer, 0, readSize);
                    if (len == -1) break;
                    rafWrite.write(buffer, 0, len);
                    remaining -= len;
                }
            }
            log.info("索引为{}分块文件已完成", index);
        }
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
        // 测试文件分片再合并
        String localFilePath = "D:\\cache\\s3\\123.mp4";
        File localFile = new File(localFilePath);
        String name = localFile.getName();
        String mainName = TiFileUtil.mainName(name);
        String extName = TiFileUtil.extName(name);
        Executor executor = Executors.newFixedThreadPool(10);
        File localChunkFolder = fileSpliceChunkAsync(localFile, 10, executor);
        String newFileName = mainName + "-" + TiIdUtil.ulid() + TiStrConst.DOT + extName;
        String newFilePath = localChunkFolder.getParent() + File.separator + newFileName;
        File newFile = new File(newFilePath);
        composeLocalObject(localChunkFolder, newFile);
    }

}
