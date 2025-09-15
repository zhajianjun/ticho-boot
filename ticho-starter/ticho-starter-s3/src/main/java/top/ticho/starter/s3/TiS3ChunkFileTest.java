package top.ticho.starter.s3;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import top.ticho.starter.s3.component.TiS3Template;
import top.ticho.starter.s3.prop.TiS3Property;
import top.ticho.tool.core.TiFileUtil;
import top.ticho.tool.core.TiIdUtil;
import top.ticho.tool.core.constant.TiStrConst;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public static void main(String[] args) throws IOException {
        initS3Template();
        uploadChunkObject();
    }

    private static void uploadChunkObject() throws IOException {
        String bucket = tiS3Template.getTiS3Property().getDefaultBucket();
        String filePath = "D:\\cache\\s3\\123.mp4";
        File file = new File(filePath);
        String fileName = file.getName();
        String mimeType = TiFileUtil.getMimeType(fileName);
        String uploadId = tiS3Template.createMultipartUpload(bucket, fileName);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            List<CompletedPart> completedParts = tiS3Template.putMultipartObject(bucket, fileName, uploadId, mimeType, Collections.emptyMap(), fileInputStream);
            tiS3Template.composeObject(bucket, fileName, uploadId, completedParts, mimeType, Collections.emptyMap());
        }
    }

    private static void putObject() throws IOException {
        String bucket = tiS3Template.getTiS3Property().getDefaultBucket();
        String filePath = "D:\\cache\\s3\\123.mp4";
        File file = new File(filePath);
        String fileName = TiFileUtil.getName(filePath);
        String mimeType = TiFileUtil.getMimeType(fileName);
        String objectname = TiIdUtil.ulid() + TiStrConst.DOT + TiFileUtil.extName(fileName);
        Map<String, String> metadata = Collections.singletonMap("filename", fileName);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            tiS3Template.putObject(bucket, objectname, mimeType, metadata, fileInputStream);
        }
    }

    private static void uploadObject() {
        String bucket = tiS3Template.getTiS3Property().getDefaultBucket();
        String filePath = "D:\\cache\\s3\\123.mp4";
        File file = new File(filePath);
        String fileName = file.getName();
        String mimeType = TiFileUtil.getMimeType(fileName);
        tiS3Template.uploadObject(bucket, fileName, mimeType, null, file.getAbsolutePath());
    }

    private static void initS3Template() {
        TiS3Property tiMinioProperty = new TiS3Property();
        tiMinioProperty.setEndpoint("http://127.0.0.1:9000");
        tiMinioProperty.setAccessKey("root");
        tiMinioProperty.setSecretKey("abcd1234");
        tiMinioProperty.setDefaultBucket("test");
        tiMinioProperty.setRegion(Region.CN_NORTH_1.id());
        TiS3ChunkFileTest.tiS3Template = new TiS3Template(tiMinioProperty);
    }

}
