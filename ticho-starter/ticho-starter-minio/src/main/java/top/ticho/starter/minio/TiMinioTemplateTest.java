package top.ticho.starter.minio;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.ticho.starter.minio.component.TiMinioTemplate;
import top.ticho.starter.minio.prop.TiMinioProperty;
import top.ticho.starter.minio.util.ChunkFileUtil;

import java.io.File;
import java.io.IOException;
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
        ChunkFileUtil.composeLocalObject(localChunkFolderFilePath, newFilePath);
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
        ChunkFileUtil.fileSpliceChunk(localFile, 5);
        return tiMinioTemplateTest;
    }

}
