package top.ticho.starter.s3.component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
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
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListPartsRequest;
import software.amazon.awssdk.services.s3.model.ListPartsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
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
import top.ticho.starter.s3.prop.TiS3Property;
import top.ticho.starter.view.enums.TiBizErrorCode;
import top.ticho.starter.view.exception.TiBizException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * S3文件下载
 *
 * @author zhajianjun
 * @date 2025-09-06 15:14
 */
@SuppressWarnings("all")
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
        Boolean forcePathStyle = tiS3Property.getForcePathStyle();
        URI endpointOverride = URI.create(endpoint);
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        Region region = Region.of(tiS3Property.getRegion());
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        this.tiS3Property = tiS3Property;
        this.s3Client = S3Client.builder().endpointOverride(endpointOverride).region(region).credentialsProvider(credentialsProvider).forcePathStyle(forcePathStyle).build();
        this.s3Presigner = S3Presigner.builder().endpointOverride(endpointOverride).region(region).credentialsProvider(credentialsProvider).build();
        this.s3AsyncClient = S3AsyncClient.builder().endpointOverride(endpointOverride).region(region).credentialsProvider(credentialsProvider).forcePathStyle(forcePathStyle).build();
        this.s3TransferManager = S3TransferManager.builder().s3Client(s3AsyncClient).build();
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
     * @param bucketName bucketName
     * @return true-存在，false-不存在
     */
    public boolean bucketExists(String bucketName) {
        try {
            HeadBucketRequest request = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (NoSuchBucketException e) {
            log.warn("bucket={}不存在", bucketName);
            return false;
        } catch (S3Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件存储桶是否存在异常", e);
        }
    }

    /**
     * 创建bucket
     *
     * @param bucketName bucketName
     */
    public void createBucket(String bucketName) {
        try {
            if (this.bucketExists(bucketName)) {
                return;
            }
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "创建文件桶异常", e);
        }
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    public void removeBucket(String bucketName) {
        try {
            s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "删除文件桶异常", e);
        }
    }

    /**
     * 根据bucketName获取信息
     *
     * @param bucketName bucket名称
     */
    public Bucket getBucket(String bucketName) {
        return this.listBuckets().buckets().stream().filter(b -> b.name().equals(bucketName)).findAny().orElse(null);
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

    /**
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param metadata    用户自定义数据
     * @param inputStream 文件流
     */
    public void putObject(String bucketName, String objectName, String contentType, Map<String, String> metadata, InputStream inputStream) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
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
     * 上传文件
     *
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param metadata      用户自定义数据
     * @param multipartFile 文件
     */
    public void putObject(String bucketName, String objectName, Map<String, String> metadata, MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .metadata(metadata)
                .contentType(multipartFile.getContentType())
                .build();
            RequestBody requestBody = RequestBody.fromInputStream(inputStream, multipartFile.getSize());
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (IOException e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件IO异常", e);
        } catch (SdkException e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件SDK异常", e);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件异常", e);
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param metadata    用户自定义数据
     * @param inputStream 文件流
     */
    public void putLargeObject(String bucketName, String objectName, Map<String, String> metadata, MultipartFile multipartFile, ExecutorService executorService) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            AsyncRequestBody requestBody = AsyncRequestBody.fromInputStream(inputStream, multipartFile.getSize(), executorService);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .metadata(metadata)
                .contentType(multipartFile.getContentType())
                .build();
            UploadRequest uploadFileRequest = UploadRequest.builder()
                .putObjectRequest(b -> b.bucket(bucketName).key(objectName))
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
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param metadata    用户自定义数据
     * @param inputStream 文件流
     */
    public void putLargeObject(String bucketName, String objectName, String contentType, Map<String, String> metadata, InputStream inputStream, ExecutorService executorService) {
        try {
            long available = inputStream.available();
            AsyncRequestBody requestBody = AsyncRequestBody.fromInputStream(inputStream, available, executorService);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .metadata(metadata)
                .contentType(contentType)
                .build();
            UploadRequest uploadFileRequest = UploadRequest.builder()
                .putObjectRequest(b -> b.bucket(bucketName).key(objectName))
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
     * 本地上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param metadata    用户自定义数据
     * @param inputStream 文件流
     */
    public void uploadObject(String bucketName, String objectName, String contentType, Map<String, String> metadata, String filePath) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .metadata(metadata)
                .contentType(contentType)
                .build();
            RequestBody requestBody = RequestBody.fromFile(Paths.get(filePath));
            s3Client.putObject(putObjectRequest, requestBody);
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件异常", e);
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     */
    public void removeObject(String bucketName, String objectName) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(objectName).build());
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "删除文件异常", e);
        }
    }

    /**
     * 批量删除对象
     *
     * @param bucketName
     * @param objectNames
     * @return true/false
     */
    public void removeObjects(String bucketName, List<String> objectNames) {
        List<ObjectIdentifier> identifiers = new ArrayList<>(objectNames.size());
        for (String objectName : objectNames) {
            identifiers.add(ObjectIdentifier.builder().key(objectName).build());
        }
        Delete delete = Delete.builder()
            .objects(identifiers)
            .build();
        DeleteObjectsRequest objectsArgs = DeleteObjectsRequest.builder()
            .bucket(bucketName)
            .delete(delete)
            .build();
        boolean isError = false;
        DeleteObjectsResponse results = s3Client.deleteObjects(objectsArgs);
        for (DeletedObject item : results.deleted()) {
            try {
                log.info("Error in deleting object " + item.key() + "; " + item.versionId());
            } catch (Exception e) {
                log.error("批量删除对象异常，{}", e.getMessage(), e);
                isError = true;
            }
        }
        if (isError) {
            throw new TiBizException(TiBizErrorCode.FAIL, "批量删除对象异常");
        }
    }

    /**
     * 获取分片名称地址HashMap key=分片序号 value=分片文件地址
     *
     * @param bucketName 存储桶名称
     * @param objectMd5  对象Md5
     * @return objectChunkNameMap
     */
    public Map<Integer, String> mapChunkObjectNames(String bucketName, String objectMd5) {
        if (null == bucketName) {
            bucketName = tiS3Property.getChunkBucket();
        }
        if (null == objectMd5) {
            return null;
        }
        List<String> chunkPaths = listObjectNames(bucketName, objectMd5, 1000, true);
        if (null == chunkPaths || chunkPaths.size() == 0) {
            return null;
        }
        Map<Integer, String> chunkMap = new HashMap<>(chunkPaths.size());
        for (String chunkName : chunkPaths) {
            Integer partNumber = Integer.parseInt(chunkName.substring(chunkName.indexOf("/") + 1, chunkName.lastIndexOf(".")));
            chunkMap.put(partNumber, chunkName);
        }
        return chunkMap;
    }

    public String createMultipartUpload(String bucketName, String objectName) {
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(objectName)
            .build();
        CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
        return createResponse.uploadId();
    }

    public List<CompletedPart> putMultipartObject(
        String bucketName,
        String objectName,
        String uploadId,
        String contentType,
        Map<String, String> metadata,
        InputStream inputStream
    ) {
        List<CompletedPart> completedParts = new ArrayList<>();
        long partSize = tiS3Property.getPartSize();
        int partNumber = 1;
        try {
            byte[] buffer = new byte[(int) partSize];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                if (bytesRead < partSize) {
                    // 处理最后一块可能不满的情况
                    byte[] lastBuffer = new byte[bytesRead];
                    System.arraycopy(buffer, 0, lastBuffer, 0, bytesRead);
                    buffer = lastBuffer;
                }
                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .build();
                UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest, RequestBody.fromBytes(buffer));
                String eTag = uploadPartResponse.eTag();
                completedParts.add(CompletedPart.builder()
                    .partNumber(partNumber)
                    .eTag(eTag)
                    .build());
                partNumber++;
            }
        } catch (IOException e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "上传分片文件异常", e);
        }
        return completedParts;
    }

    public List<CompletedPart> getCompletedPartsForUpload(String bucketName, String objectName, String uploadId) {
        try {
            ListPartsRequest listPartsRequest = ListPartsRequest.builder()
                .bucket(bucketName)
                .key(objectName)
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
     * 分块文件必须大于5mb
     *
     * @param chunkBucKetName   分片文件所在存储桶名称
     * @param composeBucketName 合并后的对象文件存储的存储桶名称
     * @param chunkNames        分片文件名称集合
     * @param objectName        合并后的对象文件名称
     */
    public void composeObject(
        String bucketName,
        String objectName,
        String uploadId,
        List<CompletedPart> completedParts,
        String contentType,
        Map<String, String> metadata
    ) {
        try {
            CompletedMultipartUpload completedUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(objectName)
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
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    public ResponseInputStream<GetObjectResponse> getObject(String bucketName, String objectName) {
        try {
            return s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(objectName).build());
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常", e);
        }
    }

    /**
     * 文件下载
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度 (可选，如果无值则代表读到文件结尾)
     * @return 二进制流
     */
    public ResponseInputStream<GetObjectResponse> getObject(String bucketName, String objectName, Long offset, Long length) {
        try {
            return s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build());
        } catch (Exception e) {
            log.error("文件下载异常，{}", e.getMessage(), e);
            // if (e instanceof ErrorResponseException ex) {
            //     if (ex.errorResponse().code().equals("NoSuchKey")) {
            //         throw new TiBizException(TiBizErrorCode.FAIL);
            //     }
            // }
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常", e);
        }
    }

    /**
     * 根据文件前缀查询文件
     *
     * @param bucketName bucket名称
     * @param prefix     前缀
     * @param recursive  是否递归查询,true-则查询文件，包含文件夹 false-查询所有文件，不包含文件夹
     * @return MinioItem 列表
     */
    public List<S3Object> listObjects(String bucketName, String prefix, Integer maxKeys) {
        List<S3Object> objectList = new ArrayList<>();
        try {
            ListObjectsResponse response = s3Client.listObjects(ListObjectsRequest.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .maxKeys(maxKeys)
                .build());

            for (S3Object result : response.contents()) {
                objectList.add(result);
            }
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件信息异常", e);
        }
        return objectList;
    }

    /**
     * 获取对象文件名称列表
     *
     * @param bucketName 存储桶名称
     * @param prefix     对象名称前缀
     * @param sort       是否排序(升序)
     * @return objectNames
     */
    public List<String> listObjectNames(String bucketName, String prefix, Integer maxKeys, Boolean sort) {
        List<S3Object> chunks = listObjects(bucketName, prefix, maxKeys);
        try {
            List<String> chunkPaths = new ArrayList<>();
            for (S3Object item : chunks) {
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
     * @param bucketName bucketName
     * @param objectName 文件名称
     * @param expires    过期时间 <=7天，默认30分钟，单位：秒
     * @return String
     */
    public String getObjectUrl(String bucketName, String objectName, Integer expires) {
        try {
            TimeUnit timeUnit = TimeUnit.SECONDS;
            if (Objects.isNull(expires)) {
                expires = 30;
                timeUnit = TimeUnit.MINUTES;
            }
            // TimeUnit转Duration
            Duration expiration = Duration.of(expires, timeUnit.toChronoUnit());
            GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();
            GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();
            PresignedGetObjectRequest objectRequest = s3Presigner.presignGetObject(request);
            return objectRequest.url().toString();
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "获取文件外链异常", e);
        }
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucketName
     * @param objectName 文件名称
     * @param expires    过期时间 <=7天
     * @param timeUnit   时间单位
     * @return String
     */
    public String getObjectUrl(String bucketName, String objectName, Integer expires, TimeUnit timeUnit) {
        try {
            if (Objects.isNull(expires)) {
                expires = 24;
                timeUnit = TimeUnit.HOURS;
            }
            // TimeUnit转Duration
            Duration expiration = Duration.of(expires, timeUnit.toChronoUnit());
            GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();
            GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(getObjectRequest)
                .build();
            PresignedGetObjectRequest objectRequest = s3Presigner.presignGetObject(request);
            return objectRequest.url().toString();
        } catch (Exception e) {
            throw new TiBizException(TiBizErrorCode.FAIL, "获取文件外链异常", e);
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

}
