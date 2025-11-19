package top.ticho.tool.s3;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;
import top.ticho.tool.core.TiUrlUtil;
import top.ticho.tool.core.enums.TiBizErrorCode;
import top.ticho.tool.core.exception.TiBizException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * S3文件下载
 *
 * @author zhajianjun
 * @date 2025-10-29 19:27
 */
@Data
@Slf4j
public class TiS3Template {
    private final TiS3Property tiS3Property;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3AsyncClient s3AsyncClient;
    private final S3TransferManager s3TransferManager;

    public TiS3Template(TiS3Property tiS3Property) {
        String endpoint = tiS3Property.getEndpoint();
        String accessKey = tiS3Property.getAccessKey();
        String secretKey = tiS3Property.getSecretKey();
        Boolean pathStyleAccess = tiS3Property.getPathStyleAccess();
        URI endpointOverride = URI.create(endpoint);
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        Region region = Region.of(tiS3Property.getRegion());
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        S3Configuration s3Configuration = S3Configuration.builder()
            .chunkedEncodingEnabled(false)
            .pathStyleAccessEnabled(pathStyleAccess)
            .build();
        this.tiS3Property = tiS3Property;
        this.s3Client = S3Client.builder()
            .endpointOverride(endpointOverride)
            .region(region)
            .credentialsProvider(credentialsProvider)
            .serviceConfiguration(s3Configuration)
            .build();
        this.s3Presigner = S3Presigner.builder()
            .endpointOverride(endpointOverride)
            .region(region)
            .s3Client(s3Client)
            .credentialsProvider(credentialsProvider)
            .serviceConfiguration(s3Configuration)
            .build();
        this.s3AsyncClient = S3AsyncClient.builder()
            .endpointOverride(endpointOverride)
            .region(region)
            .credentialsProvider(credentialsProvider)
            .serviceConfiguration(s3Configuration)
            .build();
        this.s3TransferManager = S3TransferManager.builder()
            .s3Client(s3AsyncClient)
            .build();
    }

    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
        if (s3Presigner != null) {
            s3Presigner.close();
        }
        if (s3AsyncClient != null) {
            s3AsyncClient.close();
        }
        if (s3TransferManager != null) {
            s3TransferManager.close();
        }
    }

    /**
     * 查询文件存储桶是否存在
     *
     * @param bucket 存储桶
     * @return true-存在，false-不存在
     */
    public boolean bucketExists(String bucket) {
        try {
            HeadBucketRequest request = HeadBucketRequest.builder()
                .bucket(bucket)
                .build();
            s3Client.headBucket(request);
            return true;
        } catch (NoSuchBucketException e) {
            log.warn("bucket={}不存在", bucket);
            return false;
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件存储桶是否存在异常", e);
        }
    }

    /**
     * 创建bucket
     *
     * @param bucket 存储桶
     */
    public void createBucket(String bucket) {
        try {
            if (this.bucketExists(bucket)) {
                return;
            }
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "创建文件桶异常", e);
        }
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucket 存储桶
     */
    public void removeBucket(String bucket) {
        try {
            s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucket).build());
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "删除文件桶异常", e);
        }
    }

    /**
     * 根据bucketName获取信息
     *
     * @param bucket 存储桶
     */
    public Bucket getBucket(String bucket) {
        try {
            ListBucketsRequest bucketsRequest = ListBucketsRequest.builder()
                .prefix(bucket)
                .build();
            return s3Client.listBuckets(bucketsRequest).buckets().stream().filter(b -> b.name().equals(bucket)).findAny().orElse(null);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "查询全部文件桶异常", e);
        }
    }

    /**
     * 获取全部bucket
     */
    public ListBucketsResponse listBuckets() {
        try {
            return s3Client.listBuckets();
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "查询全部文件桶异常", e);
        }
    }

    public boolean objectExists(String bucket, String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            log.warn("bucket={} object={}不存在", bucket, key);
            return false;
        } catch (S3Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件是否存在异常", e);
        }
    }

    /**
     * 获取对象元数据
     *
     * @param bucket 存储桶
     * @param key    存储对象key
     * @return {@link HeadObjectResponse }
     */
    public HeadObjectResponse getObjectMetadata(String bucket, String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
            return s3Client.headObject(request);
        } catch (NoSuchKeyException e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件元数据异常，文件不存在", e);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件元数据异常", e);
        }
    }

    /**
     * 上传文件
     *
     * @param bucket      存储桶
     * @param key         存储文件key
     * @param contentType 内容类型
     * @param metadata    用户自定义数据
     * @param inputStream 文件流
     */
    public void putObject(String bucket, String key, String contentType, Map<String, String> metadata, InputStream inputStream) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(metadata)
                .contentType(contentType)
                .build();
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, inputStream.available());
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件异常", e);
        }
    }

    /**
     * 上传本地文件
     *
     * @param bucket   存储桶
     * @param key      存储文件key
     * @param metadata 用户自定义数据
     * @param filePath 文件路径
     */
    public void putObjectFromFile(String bucket, String key, Map<String, String> metadata, String filePath) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(metadata)
                .build();
            RequestBody requestBody = RequestBody.fromFile(Paths.get(filePath));
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件异常", e);
        }
    }

    /**
     * 上传文本文件
     *
     * @param bucket  存储桶
     * @param key     存储文件key
     * @param content 内容
     */
    public void putObjectAsString(String bucket, String key, String content) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
            RequestBody requestBody = RequestBody.fromString(content);
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件异常", e);
        }
    }

    /**
     * 上传文件
     *
     * @param bucket          存储桶
     * @param key             存储文件key
     * @param contentType     内容类型
     * @param metadata        用户自定义数据
     * @param inputStream     文件流
     * @param executorService executor
     */
    public void putObjectAsync(String bucket, String key, String contentType, Map<String, String> metadata, InputStream inputStream, ExecutorService executorService) {
        try {
            long available = inputStream.available();
            AsyncRequestBody requestBody = AsyncRequestBody.fromInputStream(inputStream, available, executorService);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(metadata)
                .contentType(contentType)
                .build();
            UploadRequest uploadFileRequest = UploadRequest.builder()
                .putObjectRequest(b -> b.bucket(bucket).key(key))
                .putObjectRequest(putObjectRequest)
                .requestBody(requestBody)
                .build();
            Upload upload = s3TransferManager.upload(uploadFileRequest);
            upload.completionFuture().join();
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件异常", e);
        }
    }

    /**
     * 删除文件
     *
     * @param bucket 存储桶
     * @param key    存储文件key
     */
    public void removeObject(String bucket, String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "删除文件异常", e);
        }
    }

    /**
     * 批量删除对象
     *
     * @param bucket 存储桶
     * @param keys   文件名称集合
     */
    public void removeObjects(String bucket, List<String> keys) {
        List<ObjectIdentifier> identifiers = new ArrayList<>(keys.size());
        for (String key : keys) {
            identifiers.add(ObjectIdentifier.builder().key(key).build());
        }
        Delete delete = Delete.builder()
            .objects(identifiers)
            .build();
        DeleteObjectsRequest objectsArgs = DeleteObjectsRequest.builder()
            .bucket(bucket)
            .delete(delete)
            .build();
        boolean isError = false;
        DeleteObjectsResponse results = s3Client.deleteObjects(objectsArgs);
        Exception e = null;
        for (DeletedObject item : results.deleted()) {
            try {
                log.info("Error in deleting object {}; {}", item.key(), item.versionId());
            } catch (Exception ex) {
                isError = true;
                e = ex;
            }
        }
        if (isError) {
            throw new TiBizException(TiBizErrorCode.FAIL, "批量删除对象异常", e);
        }
    }

    /**
     * 创建分片上传
     *
     * @param bucket      存储桶
     * @param key         存储文件key
     * @param contentType 内容类型
     * @param metadata    元数据
     * @return {@link String }
     */
    public String createMultipartUpload(String bucket, String key, String contentType, Map<String, String> metadata) {
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .metadata(metadata)
            .build();
        CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
        return createResponse.uploadId();
    }

    /**
     * 上传分片文件
     *
     * @param bucket      存储桶
     * @param key         存储对象key
     * @param uploadId    上传ID
     * @param partNumber  分片编号
     * @param inputStream 输入流
     * @return {@link CompletedPart }
     */
    public CompletedPart putMultipartObject(
        String bucket,
        String key,
        String uploadId,
        int partNumber,
        InputStream inputStream
    ) {
        try {
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();
            UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));
            String eTag = uploadPartResponse.eTag();
            return CompletedPart.builder()
                .partNumber(partNumber)
                .eTag(eTag)
                .build();
        } catch (IOException e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传分片文件异常", e);
        }
    }

    /**
     * 获取上传分片文件信息
     *
     * @param bucket   存储桶
     * @param key      存储对象key
     * @param uploadId 上传ID
     * @return {@link List }<{@link CompletedPart }>
     */
    public List<CompletedPart> getCompletedPartsForUpload(String bucket, String key, String uploadId) {
        try {
            ListPartsRequest listPartsRequest = ListPartsRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .build();
            ListPartsResponse listPartsResponse = s3Client.listParts(listPartsRequest);
            List<CompletedPart> completedParts = new ArrayList<>();
            for (Part part : listPartsResponse.parts()) {
                CompletedPart completedPart = CompletedPart.builder()
                    .partNumber(part.partNumber())
                    .eTag(part.eTag())
                    .build();
                completedParts.add(completedPart);
            }
            return completedParts;
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "获取已完成分片列表异常", e);
        }
    }

    /**
     * 合并文件
     *
     * @param bucket         存储桶
     * @param key            存储文件key
     * @param uploadId       上传ID
     * @param completedParts 已完成零件
     */
    public void composeObject(
        String bucket,
        String key,
        String uploadId,
        List<CompletedPart> completedParts
    ) {
        try {
            CompletedMultipartUpload completedUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(completedUpload)
                .build();
            s3Client.completeMultipartUpload(completeRequest);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "合并文件异常", e);
        }
    }

    /**
     * 文件下载
     *
     * @param bucket 存储桶
     * @param key    存储文件key
     * @return 二进制流
     */
    public ResponseInputStream<GetObjectResponse> getObject(String bucket, String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket(bucket)
                .key(key)
                .build();
            return s3Client.getObject(getObjectRequest);
        } catch (NoSuchKeyException e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常, 文件不存在", e);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常", e);
        }
    }

    /**
     * 文件下载
     *
     * @param bucket 存储桶
     * @param key    存储文件key
     * @param range  范围
     * @return 二进制流
     *
     * <p>range用法<p/>
     * <pre>
     * "bytes=0-499"：下载第0到第499个字节（前500字节）。
     * "bytes=500-999"：下载第500到第999个字节。
     * "bytes=500-"：从第500个字节下载到文件末尾。
     * "bytes=-500"：下载最后500个字节。
     * <pre/>
     */
    public ResponseInputStream<GetObjectResponse> getObject(String bucket, String key, String range) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .range(range)
                .bucket(bucket)
                .key(key)
                .build();
            return s3Client.getObject(getObjectRequest);
        } catch (NoSuchKeyException e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常, 文件不存在", e);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常", e);
        }
    }

    /**
     * 获取字符串对象
     *
     * @param bucket 存储桶
     * @param key    存储文件key
     * @return {@link String }
     */
    public String getObjectAsString(String bucket, String key) {
        try (ResponseInputStream<GetObjectResponse> response = getObject(bucket, key)) {
            return new String(response.readAllBytes(), StandardCharsets.UTF_8);
        } catch (NoSuchKeyException e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常, 文件不存在", e);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常", e);
        }
    }

    /**
     * 拷贝文件
     *
     * @param sourceBucket 源桶名称
     * @param sourceObject 源文件
     * @param targetBucket 目标桶名称
     * @param targetObject 目标文件
     */
    public void copyObject(String sourceBucket, String sourceObject, String targetBucket, String targetObject) {
        try {
            CopyObjectRequest copyObjectArgs = CopyObjectRequest.builder()
                .sourceBucket(sourceBucket)
                .sourceKey(sourceObject)
                .destinationBucket(targetBucket)
                .destinationKey(targetObject)
                .build();
            s3Client.copyObject(copyObjectArgs);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "拷贝文件异常", e);
        }
    }

    /**
     * 根据文件前缀查询文件
     *
     * @param bucket  存储桶
     * @param prefix  前缀
     * @param maxKeys 最大键数
     * @return S3Object 列表
     */
    public ListObjectsResponse listObjects(String bucket, String prefix, String marker, Integer maxKeys) {
        try {
            ListObjectsRequest request = ListObjectsRequest.builder()
                .bucket(bucket)
                .prefix(prefix)
                .marker(marker)
                .maxKeys(maxKeys)
                .build();
            return s3Client.listObjects(request);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件信息异常", e);
        }
    }

    /**
     * 获取对象文件名称列表
     *
     * @param bucket 存储桶
     * @param prefix 对象名称前缀
     * @param sort   是否排序(升序)
     * @return objectNames
     */
    public List<String> listObjectNames(String bucket, String prefix, Integer maxKeys, Boolean sort) {
        ListObjectsResponse response = listObjects(bucket, prefix, "", maxKeys);
        try {
            List<String> chunkPaths = new ArrayList<>();
            for (S3Object item : response.contents()) {
                chunkPaths.add(item.key());
            }
            if (sort) {
                Collections.sort(chunkPaths);
            }
            return chunkPaths;
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "获取对象文件名称列表异常", e);
        }
    }

    /**
     * 获取文件外链
     *
     * @param bucket  存储桶
     * @param key     存储文件key
     * @param expires 过期时间 <=7天，默认5分钟，单位：秒
     * @return String
     */
    public String getPreviewUrl(String bucket, String key, Integer expires) {
        return getPreviewUrl(bucket, key, expires, TimeUnit.SECONDS);
    }

    /**
     * 获取预览链接
     *
     * @param bucket   存储桶
     * @param key      存储文件key
     * @param expires  过期时间 <=7天，默认5分钟
     * @param timeUnit 时间单位
     * @return String
     */
    public String getPreviewUrl(String bucket, String key, Integer expires, TimeUnit timeUnit) {
        try {
            if (Objects.isNull(expires)) {
                expires = 5;
                timeUnit = TimeUnit.MINUTES;
            }
            Duration expiration = Duration.ofSeconds(timeUnit.toSeconds(expires));
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
            GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();
            PresignedGetObjectRequest objectRequest = s3Presigner.presignGetObject(request);
            return objectRequest.url().toString();
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "获取文件预览链接异常", e);
        }
    }

    /**
     * 获取预览链接
     *
     * @param bucket  存储桶
     * @param key     存储文件key
     * @param expires 过期时间 <=7天，默认5分钟，单位：秒
     * @return String
     */
    public String getDownloadUrl(String bucket, String key, String filaName, Integer expires) {
        return getDownloadUrl(bucket, key, filaName, expires, TimeUnit.SECONDS);
    }

    /**
     * 获取预览链接
     *
     * @param bucket   存储桶
     * @param key      存储文件key
     * @param expires  过期时间 <=7天，默认5分钟
     * @param timeUnit 时间单位
     * @return String
     */
    public String getDownloadUrl(String bucket, String key, String filaName, Integer expires, TimeUnit timeUnit) {
        try {
            if (Objects.isNull(expires)) {
                expires = 5;
                timeUnit = TimeUnit.MINUTES;
            }
            Duration expiration = Duration.ofSeconds(timeUnit.toSeconds(expires));
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .responseContentDisposition("attachment;filename=" + TiUrlUtil.encode(filaName))
                .build();
            GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();
            PresignedGetObjectRequest objectRequest = s3Presigner.presignGetObject(request);
            return objectRequest.url().toString();
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "获取文件下载链接异常", e);
        }
    }

}
