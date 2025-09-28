package top.ticho.starter.minio.component;

import io.minio.BucketExistsArgs;
import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import top.ticho.starter.minio.prop.TiMinioProperty;
import top.ticho.starter.view.enums.TiBizErrorCode;
import top.ticho.starter.view.exception.TiBizException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Minio文件下载,https://docs.minio.io/cn/java-client-api-reference.html#removeObject
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40
 */
@SuppressWarnings("all")
@Data
@Slf4j
public class TiMinioTemplate {

    private TiMinioProperty tiMinioProperty;

    @Getter
    private MinioClient minioClient;

    public TiMinioTemplate(TiMinioProperty tiMinioProperty) {
        this.tiMinioProperty = tiMinioProperty;
        this.minioClient = MinioClient
            .builder()
            .credentials(tiMinioProperty.getAccessKey(), tiMinioProperty.getSecretKey())
            .endpoint(tiMinioProperty.getEndpoint())
            .build();
    }

    /**
     * 查询文件存储桶是否存在
     *
     * @param bucketName bucketName
     * @return true-存在，false-不存在
     */
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("查询文件存储桶是否存在异常，异常:{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件存储桶是否存在异常");
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
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("创建文件桶异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "创建文件桶异常");
        }
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    public void removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("删除文件桶异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "删除文件桶异常");
        }
    }

    /**
     * 根据bucketName获取信息
     *
     * @param bucketName bucket名称
     */
    public Bucket getBucket(String bucketName) {
        return this.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findAny().orElse(null);
    }

    /**
     * 获取全部bucket
     */
    public List<Bucket> listBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            log.error("查询全部文件桶异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "查询全部文件桶异常");
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName   bucket名称
     * @param objectName   文件名称
     * @param userMetadata 用户自定义数据
     * @param inputStream  文件流
     */
    public void putObject(String bucketName, String objectName, String contentType, Map<String, String> userMetadata, InputStream inputStream) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .userMetadata(userMetadata)
                // 分段上传中允许的最小分段大小为5MiB。
                .stream(inputStream, inputStream.available(), tiMinioProperty.getPartSize())
                .contentType(contentType)
                .build());
        } catch (Exception e) {
            log.error("上传文件异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件异常");
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName    bucket名称
     * @param objectName    文件名称
     * @param userMetadata  用户自定义数据
     * @param multipartFile 文件
     */
    public void putObject(String bucketName, String objectName, Map<String, String> userMetadata, MultipartFile multipartFile) {
        try {
            InputStream inputStream = multipartFile.getInputStream();
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .userMetadata(userMetadata)
                    // 分段上传中允许的最小分段大小为5MiB。
                    .stream(inputStream, multipartFile.getSize(), tiMinioProperty.getPartSize())
                    .contentType(multipartFile.getContentType())
                    .build()
            );
        } catch (Exception e) {
            log.error("上传文件异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件异常");
        }
    }

    /**
     * 本地上传文件
     *
     * @param bucketName   bucket名称
     * @param objectName   文件名称
     * @param userMetadata 用户自定义数据
     * @param inputStream  文件流
     */
    public void uploadObject(String bucketName, String objectName, String contentType, Map<String, String> userMetadata, String filename) {
        try {
            minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(filename)
                .userMetadata(userMetadata)
                .contentType(contentType)
                .build());
        } catch (Exception e) {
            log.error("上传文件异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "上传文件异常");
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
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            log.error("删除文件异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "删除文件异常");
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
        List<DeleteObject> deleteObjects = new ArrayList<>(objectNames.size());
        for (String objectName : objectNames) {
            deleteObjects.add(new DeleteObject(objectName));
        }
        RemoveObjectsArgs objectsArgs = RemoveObjectsArgs.builder()
            .bucket(bucketName)
            .objects(deleteObjects)
            .build();
        boolean isError = false;
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(objectsArgs);
        for (Result<DeleteError> result : results) {
            try {
                DeleteError error = result.get();
                log.info("Error in deleting object " + error.objectName() + "; " + error.message());
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
            bucketName = tiMinioProperty.getChunkBucket();
        }
        if (null == objectMd5) {
            return null;
        }
        List<String> chunkPaths = listObjectNames(bucketName, objectMd5, true);
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
        String chunkBucKetName,
        String composeBucketName,
        List<String> chunkNames,
        String objectName,
        String contentType,
        Map<String, String> userMetadata,
        boolean isDeleteChunkObject
    ) {
        try {
            List<ComposeSource> sourceObjectList = new ArrayList<>(chunkNames.size());
            for (String chunk : chunkNames) {
                ComposeSource composeSource = ComposeSource.builder()
                    .bucket(chunkBucKetName)
                    .object(chunk)
                    .build();
                sourceObjectList.add(composeSource);
            }
            Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeaders.CONTENT_TYPE, contentType);
            ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket(composeBucketName)
                .object(objectName)
                .sources(sourceObjectList)
                .headers(headers)
                .userMetadata(userMetadata)
                .build();
            minioClient.composeObject(composeObjectArgs);
            if (isDeleteChunkObject) {
                removeObjects(chunkBucKetName, chunkNames);
            }
        } catch (Exception e) {
            log.error("合并文件异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "合并文件异常");
        }
    }

    /**
     * 文件下载
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    public GetObjectResponse getObject(String bucketName, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            log.error("文件下载异常，{}", e.getMessage(), e);
            if (e instanceof ErrorResponseException ex) {
                if (ex.errorResponse().code().equals("NoSuchKey")) {
                    throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常");
                }
            }
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常");
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
    public GetObjectResponse getObject(String bucketName, String objectName, Long offset, Long length) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .offset(offset)
                .length(length)
                .build());
        } catch (Exception e) {
            log.error("文件下载异常，{}", e.getMessage(), e);
            if (e instanceof ErrorResponseException ex) {
                if (ex.errorResponse().code().equals("NoSuchKey")) {
                    throw new TiBizException(TiBizErrorCode.FAIL);
                }
            }
            throw new TiBizException(TiBizErrorCode.FAIL, "文件下载异常");
        }
    }

    /**
     * 查询文件信息
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 文件对象信息
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) {
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            if (e instanceof ErrorResponseException e1) {
                Response response = e1.response();
                int code = response.code();
                if (HttpURLConnection.HTTP_NOT_FOUND == code) {
                    log.warn("bucket={},object={}文件信息不存在", bucketName, objectName);
                    return null;
                }
            }
            log.error("查询文件信息异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件信息异常");
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
    public List<Result<Item>> listObjects(String bucketName, String prefix, boolean recursive) {
        List<Result<Item>> objectList = new ArrayList<>();
        try {
            Iterable<Result<Item>> objectsIterator = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .recursive(recursive)
                .build());
            for (Result<Item> result : objectsIterator) {
                objectList.add(result);
            }
        } catch (Exception e) {
            log.error("根据文件前缀查询文件信息异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "查询文件信息异常");
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
    public List<String> listObjectNames(String bucketName, String prefix, Boolean sort) {
        List<Result<Item>> chunks = listObjects(bucketName, prefix, true);
        try {
            List<String> chunkPaths = new ArrayList<>();
            for (Result<Item> item : chunks) {
                chunkPaths.add(item.get().objectName());
            }
            if (sort) {
                Collections.sort(chunkPaths);
            }
            return chunkPaths;
        } catch (Exception e) {
            log.error("获取对象文件名称列表异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "获取对象文件名称列表异常");
        }
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucketName
     * @param objectName 文件名称
     * @param expiry     过期时间 <=7天，默认30分钟，单位：秒
     * @return String
     */
    public String getObjectUrl(String bucketName, String objectName, Integer expiry) {
        try {
            TimeUnit timeUnit = TimeUnit.SECONDS;
            if (Objects.isNull(expiry)) {
                expiry = 30;
                timeUnit = TimeUnit.MINUTES;
            }
            GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(expiry, timeUnit)
                .build();
            return minioClient.getPresignedObjectUrl(urlArgs);
        } catch (Exception e) {
            log.error("获取文件外链异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "获取文件外链异常");
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
            GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry(expires, timeUnit)
                .build();
            return minioClient.getPresignedObjectUrl(urlArgs);
        } catch (Exception e) {
            log.error("获取文件外链异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "获取文件外链异常");
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
            CopyObjectArgs copyObjectArgs = CopyObjectArgs.builder()
                .bucket(targetBucket)
                .object(targetObject)
                .source(
                    CopySource.builder()
                        .bucket(sourceBucket)
                        .object(sourceObject)
                        .build()
                )
                .build();
            minioClient.copyObject(copyObjectArgs);
        } catch (Exception e) {
            log.error("拷贝文件异常，{}", e.getMessage(), e);
            throw new TiBizException(TiBizErrorCode.FAIL, "拷贝文件异常");
        }
    }

}
