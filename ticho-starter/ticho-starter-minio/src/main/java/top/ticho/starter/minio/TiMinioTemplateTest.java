package top.ticho.starter.minio;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.ticho.starter.minio.component.TiMinioTemplate;
import top.ticho.starter.minio.prop.TiMinioProperty;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Objects;

/**
 * Minio文件下载
 * 文档{@see https://docs.minio.io/cn/java-client-api-reference.html}
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@Data
@Slf4j
public class TiMinioTemplateTest {

    private TiMinioTemplate tiMinioTemplate;

    public TiMinioTemplateTest(TiMinioTemplate tiMinioTemplate) {
        this.tiMinioTemplate = tiMinioTemplate;
    }


    /**
     * 大文件分割
     *
     * @param localFile        本地大文件
     * @param localChunkFolder 本地大文件分片文件夹
     */
    public void fileSpliceChunk(File localFile, File localChunkFolder) throws IOException {
        if (!localFile.exists()) {
            log.info("文件{}不存在", localFile.getAbsolutePath());
            return;
        }
        if (!localChunkFolder.exists()) {
            boolean mkdirs = localChunkFolder.mkdirs();
            log.info("分块文件夹{}不存在，创建文件夹结果{}", localChunkFolder.getAbsolutePath(), mkdirs);
        }
        // 分块大小 1MB
        long chunkSize = 1024 * 1024 * 5;
        // 分块数量
        long chunkNum = (long) Math.ceil(localFile.length() * 1.0 / chunkSize);
        log.info("分块总数：{}", chunkNum);
        // 缓冲区大小
        byte[] bytes = new byte[1024];
        // 使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(localFile, "r");
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
                    if (chunkFile.length() >= chunkSize) {
                        break;
                    }
                }
                raf_write.close();
                log.info("索引为{}分块文件{}已完成", i, localChunkFolder.getAbsolutePath());
            }
        }
        raf_read.close();
    }

    /**
     * 合并文件到本地
     *
     * @param chunkFolderPath 分片所在文件夹路径
     * @param filePath        合并文件路径
     */
    public void composeLocalObject(String chunkFolderPath, String filePath) throws IOException {
        File folder = new File(chunkFolderPath);
        File[] files = folder.listFiles();
        if (Objects.isNull(files)) {
            return;
        }
        RandomAccessFile mergedFile = new RandomAccessFile(filePath, "rw");
        // 缓冲区大小
        byte[] bytes = new byte[1024];
        for (File file : files) {
            RandomAccessFile partFile = new RandomAccessFile(file, "r");
            int len;
            while ((len = partFile.read(bytes)) != -1) {
                mergedFile.write(bytes, 0, len);
            }
            partFile.close();
        }
        mergedFile.close();
        log.info("【{}】本地文件合并成功", filePath);
    }

    /**
     * 将分块文件上传至minio
     *
     * @param chunkFolderFile 分块文件夹
     * @param bucket          文件桶
     */
    public void uploadChunkToMinio(File chunkFolderFile, String bucket, String prefix) {
        // 分块文件
        File[] files = chunkFolderFile.listFiles();
        if (Objects.isNull(files)) {
            return;
        }
        // 将分块文件上传至minio
        for (File file : files) {
            String objectName = prefix + "/" + file.getName();
            tiMinioTemplate.uploadObject(bucket, file.getAbsolutePath(), objectName, "application/octet-stream", null);
            log.info("上传分块成功{}", objectName);
        }
    }

    /**
     * 合并minio文件
     *
     * @param chunkBucKetName     分片所在桶
     * @param composeBucketName   合并目标桶
     * @param prefix              分片文件夹
     * @param objectName          合并文件路径
     * @param contentType         文件类型
     * @param isDeleteChunkObject 是否删除分片
     */
    public void composeMinioObject(
        String chunkBucKetName,
        String composeBucketName,
        String prefix,
        String objectName,
        String contentType,
        boolean isDeleteChunkObject
    ) {
        List<String> strings = tiMinioTemplate.listObjectNames(chunkBucKetName, prefix, false);
        tiMinioTemplate.composeObject(chunkBucKetName, composeBucketName, strings, objectName, contentType, null, isDeleteChunkObject);
        log.info("【{}】minio文件合并成功", objectName);
    }


    public static void main(String[] args) throws IOException {
        String localFilePath = "/Users/zhajianjun/developing/1/055596953ae67a1e4eba6bd91a5e1e5e.zip";
        // 20240218_193644.mp4
        String fileName = FileNameUtil.getName(localFilePath);
        // 20240218_193644
        String chunkFolderFileName = FileNameUtil.mainName(localFilePath);
        String chunkBucket = "rainbow";
        File localFile = new File(localFilePath);
        String parentFilePath = localFile.getParentFile().getAbsolutePath();
        String localChunkFolderFilePath = parentFilePath + File.separator + chunkFolderFileName;
        File localChunkFolderFile = new File(localChunkFolderFilePath);
        String mimeType = FileUtil.getMimeType(localFilePath);
        TiMinioTemplateTest tiMinioTemplateTest = getMinioTemplateTest(chunkBucket, localFile, localChunkFolderFile);
        // 分片上传到minio
        tiMinioTemplateTest.uploadChunkToMinio(localChunkFolderFile, chunkBucket, chunkFolderFileName);
        // minio分片文件进行合并
        tiMinioTemplateTest.composeMinioObject(chunkBucket, chunkBucket, chunkFolderFileName, fileName, mimeType, true);
        // 本地分片进行合并
        String newFilePath = parentFilePath + File.separator + System.currentTimeMillis() + "." + FileNameUtil.extName(localFilePath);
        tiMinioTemplateTest.composeLocalObject(localChunkFolderFilePath, newFilePath);
    }

    private static TiMinioTemplateTest getMinioTemplateTest(String chunkBucket, File localFile, File localChunkFolderFile) throws IOException {
        TiMinioProperty tiMinioProperty = new TiMinioProperty();
        tiMinioProperty.setEndpoint("http://127.0.0.1:9000");
        tiMinioProperty.setAccessKey("root");
        tiMinioProperty.setSecretKey("123456");
        tiMinioProperty.setDefaultBucket(chunkBucket);
        tiMinioProperty.setChunkBucket(chunkBucket);
        TiMinioTemplate tiMinioTemplate = new TiMinioTemplate(tiMinioProperty);
        TiMinioTemplateTest tiMinioTemplateTest = new TiMinioTemplateTest(tiMinioTemplate);
        // 大文件分片
        tiMinioTemplateTest.fileSpliceChunk(localFile, localChunkFolderFile);
        return tiMinioTemplateTest;
    }

}
