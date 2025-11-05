package top.ticho.tool.s3;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import top.ticho.tool.core.TiFileUtil;
import top.ticho.tool.core.TiIdUtil;
import top.ticho.tool.core.constant.TiStrConst;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * S3文件下载测试
 *
 * @author zhajianjun
 * @date 2025-10-29 19:27
 */
@Data
@Slf4j
public class TiS3ChunkFileTest {
    private static TiS3Template tiS3Template;

    public static void main(String[] args) throws IOException {
        initS3Template();
        uploadChunkObject("D:\\cache\\s3\\123.mp4");
    }

    public static void uploadText(String objectName) throws IOException {
        initS3Template();
        String text = "hello world";
        String bucket = tiS3Template.getTiS3Property().getDefaultBucket();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(text.getBytes());
        tiS3Template.putObject(bucket, objectName, "text/plain", Collections.emptyMap(), byteArrayInputStream);
        ResponseInputStream<GetObjectResponse> object = tiS3Template.getObject(bucket, objectName);
        byte[] bytes = object.readAllBytes();
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
    }

    /**
     * 文件分片上传
     */
    public static void uploadChunkObject(String filePath) throws IOException {
        TiS3Property tiS3Property = tiS3Template.getTiS3Property();
        String bucket = tiS3Property.getDefaultBucket();
        String chunkBucket = tiS3Property.getChunkBucket();
        File file = new File(filePath);
        String fileName = file.getName();
        String mimeType = TiFileUtil.getMimeType(fileName);
        String uploadId = tiS3Template.createMultipartUpload(chunkBucket, fileName, mimeType, Collections.emptyMap());
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        // 分块文件夹
        File localChunkFolder = TiChunkFileUtil.fileSpliceChunkAsync(file, 10, executorService);
        for (File item : Objects.requireNonNull(localChunkFolder.listFiles())) {
            try (FileInputStream fileInputStream = new FileInputStream(item)) {
                String name = item.getName();
                int partNumber = Integer.parseInt(name) + 1;
                tiS3Template.putMultipartObject(chunkBucket, fileName, uploadId, partNumber, fileInputStream);
            }
        }
        List<CompletedPart> completedParts = tiS3Template.getCompletedPartsForUpload(chunkBucket, fileName, uploadId);
        tiS3Template.composeObject(chunkBucket, fileName, uploadId, completedParts);
        tiS3Template.copyObject(chunkBucket, fileName, bucket, fileName);
        tiS3Template.removeObject(chunkBucket, fileName);
        executorService.shutdown();
    }

    /**
     * 上传对象流
     */
    public static void putObject(String filePath) throws IOException {
        String bucket = tiS3Template.getTiS3Property().getDefaultBucket();
        File file = new File(filePath);
        String fileName = TiFileUtil.getName(filePath);
        String mimeType = TiFileUtil.getMimeType(fileName);
        String objectname = TiIdUtil.ulid() + TiStrConst.DOT + TiFileUtil.extName(fileName);
        Map<String, String> metadata = Collections.singletonMap("filename", fileName);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            tiS3Template.putObject(bucket, objectname, mimeType, metadata, fileInputStream);
        }
    }

    /**
     * 上传本地对象
     */
    public static void uploadObject(String filePath) {
        String bucket = tiS3Template.getTiS3Property().getDefaultBucket();
        File file = new File(filePath);
        String fileName = file.getName();
        String mimeType = TiFileUtil.getMimeType(fileName);
        tiS3Template.uploadObject(bucket, fileName, mimeType, null, file.getAbsolutePath());
    }

    public static void initS3Template() {
        TiS3Property tiS3Property = new TiS3Property();
        tiS3Property.setEndpoint("http://127.0.0.1:9000");
        tiS3Property.setAccessKey("root");
        tiS3Property.setSecretKey("abcd1234");
        tiS3Property.setDefaultBucket("test");
        tiS3Property.setRegion(Region.CN_NORTH_1.id());
        TiS3ChunkFileTest.tiS3Template = new TiS3Template(tiS3Property);
    }

}
