package top.ticho.starter.s3;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.ticho.starter.s3.component.TiS3Template;
import top.ticho.starter.s3.prop.TiS3Property;
import top.ticho.starter.s3.util.ChunkFileUtil;
import top.ticho.tool.core.TiFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * S3文件下载测试
 *
 * @author zhajianjun
 * @date 2025-09-06 15:16
 */
@Data
@Slf4j
public class TiS3ChunkFileTest {
    private static TiS3Template tiS3Template;

    /**
     * 将分块文件上传至minio
     *
     * @param chunkFolderFile 分块文件夹
     * @param bucket          文件桶
     */
    public static void uploadChunkToMinio(File chunkFolderFile, String bucket, String prefix) {
        // 分块文件
        File[] files = chunkFolderFile.listFiles();
        if (Objects.isNull(files)) {
            return;
        }
        // 将分块文件上传至minio
        for (File file : files) {
            String objectName = prefix + "/" + file.getName();
            tiS3Template.uploadObject(bucket, file.getAbsolutePath(), objectName, "application/octet-stream", null);
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
    public static void composeMinioObject(
        String chunkBucKetName,
        String composeBucketName,
        String prefix,
        String objectName,
        String contentType,
        boolean isDeleteChunkObject
    ) {
        List<String> strings = tiS3Template.listObjectNames(chunkBucKetName, prefix, false);
        tiS3Template.composeObject(chunkBucKetName, composeBucketName, strings, objectName, contentType, null, isDeleteChunkObject);
        log.info("【{}】minio文件合并成功", objectName);
    }


    public static void main(String[] args) throws IOException {
        String localFilePath = "/Users/zhajianjun/developing/1/055596953ae67a1e4eba6bd91a5e1e5e.zip";
        // 20240218_193644.mp4
        String fileName = TiFileUtil.getName(localFilePath);
        // 20240218_193644
        String chunkFolderFileName = TiFileUtil.mainName(localFilePath);
        String chunkBucket = "rainbow";
        File localFile = new File(localFilePath);
        String parentFilePath = localFile.getParentFile().getAbsolutePath();
        String localChunkFolderFilePath = parentFilePath + File.separator + chunkFolderFileName;
        File localChunkFolderFile = new File(localChunkFolderFilePath);
        String mimeType = TiFileUtil.getMimeType(localFilePath);
        fileSpliceChunk(chunkBucket, localFile);
        // 分片上传到minio
        uploadChunkToMinio(localChunkFolderFile, chunkBucket, chunkFolderFileName);
        // minio分片文件进行合并
        composeMinioObject(chunkBucket, chunkBucket, chunkFolderFileName, fileName, mimeType, true);
    }

    private static void fileSpliceChunk(String chunkBucket, File localFile) throws IOException {
        TiS3Property tiMinioProperty = new TiS3Property();
        tiMinioProperty.setEndpoint("http://127.0.0.1:9000");
        tiMinioProperty.setAccessKey("root");
        tiMinioProperty.setSecretKey("123456");
        tiMinioProperty.setDefaultBucket(chunkBucket);
        tiMinioProperty.setChunkBucket(chunkBucket);
        TiS3ChunkFileTest.tiS3Template = new TiS3Template(tiMinioProperty);
        // 大文件分片
        ChunkFileUtil.fileSpliceChunk(localFile, 5);
    }

}
