package top.ticho.boot.minio.component;

import io.minio.BucketExistsArgs;
import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
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
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.Data;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import top.ticho.boot.minio.prop.MinioProperty;
import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.exception.BizException;

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
 * @date 2022-07-13 22:40:25
 */
@SuppressWarnings("all")
@Data
public class MinioTemplate {
    private static final Logger log = LoggerFactory.getLogger(MinioTemplate.class);

    // @formatter:off

    private MinioProperty minioProperty;

    private MinioClient client;

    public MinioTemplate(MinioProperty minioProperty){
        this.minioProperty = minioProperty;
        this.client = MinioClient
            .builder()
            .credentials(minioProperty.getAccessKey(), minioProperty.getSecretKey())
            .endpoint(minioProperty.getEndpoint())
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
            return client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("查询文件存储桶是否存在异常，异常:{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "查询文件存储桶是否存在异常");
        }
    }

    /**
     * 创建bucket
     *
     * @param bucketName bucketName
     */
    public void createBucket(String bucketName) {
        try {
            if(this.bucketExists(bucketName)){
                return;
            }
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("创建文件桶异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "创建文件桶异常");
        }
    }

    /**
     * 根据bucketName删除信息
     *
     * @param bucketName bucket名称
     */
    public void removeBucket(String bucketName) {
        try {
            client.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("删除文件桶异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "删除文件桶异常");
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
            return client.listBuckets();
        } catch (Exception e) {
            log.error("查询全部文件桶异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "查询全部文件桶异常");
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param userMetadata 用户自定义数据
     * @param stream     文件流
     */
    public void putObject(String bucketName, String objectName, String contentType, Map<String, String> userMetadata, InputStream stream) {
        try {
            client.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .userMetadata(userMetadata)
                // 分段上传中允许的最小分段大小为5MiB。
                .stream(stream, stream.available(), 5242880L)
                .contentType(contentType)
                .build());
        } catch (Exception e) {
            log.error("上传文件异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "上传文件异常");
        }
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param userMetadata 用户自定义数据
     * @param multipartFile  文件
     */
    public void putObject(String bucketName, String objectName, Map<String, String> userMetadata, MultipartFile multipartFile) {
        try {
            InputStream stream = multipartFile.getInputStream();
            client.putObject(
                PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .userMetadata(userMetadata)
                // 分段上传中允许的最小分段大小为5MiB。
                .stream(stream, stream.available(), 5242880L)
                .contentType(multipartFile.getContentType()).build()
            );
        } catch (Exception e) {
            log.error("上传文件异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "上传文件异常");
        }
    }

    /**
     * 本地上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param userMetadata 用户自定义数据
     * @param stream     文件流
     */
    public void uploadObject(String bucketName, String filename, String objectName, String contentType, Map<String, String> userMetadata) {
        try {
            client.uploadObject(UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(filename)
                .userMetadata(userMetadata)
                .contentType(contentType)
                .build());
        } catch (Exception e) {
            log.error("上传文件异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "上传文件异常");
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
            client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            log.error("删除文件异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "删除文件异常");
        }
    }

    /**
     * 批量删除对象
     *
     * @param bucketName
     * @param objectNames
     * @return true/false
     */
    public void removeObjects(String bucketName, List<String> objectNames){
        List<DeleteObject> deleteObjects = new ArrayList<>(objectNames.size());
        for (String objectName : objectNames){
            deleteObjects.add(new DeleteObject(objectName));
        }
        RemoveObjectsArgs objectsArgs = RemoveObjectsArgs.builder()
            .bucket(bucketName)
            .objects(deleteObjects)
            .build();
        client.removeObjects(objectsArgs);
    }

    /**
     * 获取对象文件名称列表
     *
     * @param bucketName 存储桶名称
     * @param prefix 对象名称前缀
     * @param sort 是否排序(升序)
     * @return objectNames
     */
    public List<String> listObjectNames(String bucketName, String prefix, Boolean sort) {
        List<Result<Item>> chunks = listObjects(bucketName, prefix, true);
        try {
            List<String> chunkPaths = new ArrayList<>();
            for (Result<Item> item : chunks){
                chunkPaths.add(item.get().objectName());
            }
            if (sort) {
                Collections.sort(chunkPaths);
            }
            return chunkPaths;
        } catch (Exception e) {
            log.error("获取对象文件名称列表异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "获取对象文件名称列表异常");
        }
    }

    /**
     * 获取分片名称地址HashMap key=分片序号 value=分片文件地址
     * @param bucketName 存储桶名称
     * @param objectMd5 对象Md5
     * @return objectChunkNameMap
     */
    public Map<Integer,String> mapChunkObjectNames(String bucketName, String objectMd5){
        if (null == bucketName){
            bucketName = minioProperty.getChunkBucket();
        }
        if (null == objectMd5){
            return null;
        }
        List<String> chunkPaths = listObjectNames(bucketName,objectMd5, true);
        if (null == chunkPaths || chunkPaths.size() == 0){
            return null;
        }
        Map<Integer,String> chunkMap = new HashMap<>(chunkPaths.size());
        for (String chunkName : chunkPaths) {
            Integer partNumber = Integer.parseInt(chunkName.substring(chunkName.indexOf("/") + 1,chunkName.lastIndexOf(".")));
            chunkMap.put(partNumber,chunkName);
        }
        return chunkMap;
    }

    /**
     * 合并文件
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
        boolean isDeleteChunkObject
    ){
        try {
            List<ComposeSource> sourceObjectList = new ArrayList<>(chunkNames.size());
            for (String chunk : chunkNames){
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
                .build();
            client.composeObject(composeObjectArgs);
            if(isDeleteChunkObject){
                removeObjects(chunkBucKetName, chunkNames);
            }
        } catch (Exception e) {
            log.error("合并文件异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "合并文件异常");
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
            return client.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            log.error("文件下载异常，{}", e.getMessage(), e);
            if (e instanceof ErrorResponseException) {
                ErrorResponseException ex = (ErrorResponseException) e;
                if (ex.errorResponse().code().equals("NoSuchKey")) {
                    throw new BizException(BizErrCode.FAIL, "文件下载异常");
                }
            }
            throw new BizException(BizErrCode.FAIL, "文件下载异常");
        }
    }

    /**
     * 文件下载
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param offset 起始字节的位置
     * @param length 要读取的长度 (可选，如果无值则代表读到文件结尾)
     * @return 二进制流
     */
    public GetObjectResponse getObject(String bucketName, String objectName, Long offset, Long length) {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .offset(offset)
                    .length(length)
                    .build());
        } catch (Exception e) {
            log.error("文件下载异常，{}", e.getMessage(), e);
            if (e instanceof ErrorResponseException) {
                ErrorResponseException ex = (ErrorResponseException) e;
                if (ex.errorResponse().code().equals("NoSuchKey")) {
                    throw new BizException(BizErrCode.FAIL);
                }
            }
            throw new BizException(BizErrCode.FAIL, "文件下载异常");
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
            return client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            if (e instanceof ErrorResponseException) {
                ErrorResponseException e1 = (ErrorResponseException) e;
                Response response = e1.response();
                int code = response.code();
                if (HttpURLConnection.HTTP_NOT_FOUND == code) {
                    log.warn("bucket={},object={}文件信息不存在", bucketName, objectName);
                    return null;
                }
            }
            log.error("查询文件信息异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "查询文件信息异常");
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
            Iterable<Result<Item>> objectsIterator = client.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .recursive(recursive)
                .build());
            for (Result<Item> result : objectsIterator) {
                objectList.add(result);
            }
        } catch (Exception e) {
            log.error("根据文件前缀查询文件信息异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "查询文件信息异常");
        }
        return objectList;
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucketName
     * @param objectName 文件名称
     * @param expiry 过期时间 <=7天，默认30分钟，单位：秒
     * @return String
     */
    public String getObjectUrl(String bucketName, String objectName, Integer expiry) {
        try {
            TimeUnit timeUnit = TimeUnit.SECONDS;
            if(Objects.isNull(expiry)){
                expiry = 30;
                timeUnit = TimeUnit.MINUTES;
            }
            GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs.builder()
                 .method(Method.GET)
                 .bucket(bucketName)
                 .object(objectName)
                 .expiry(expiry, timeUnit)
                 .build();
            return client.getPresignedObjectUrl(urlArgs);
        } catch (Exception e) {
            log.error("获取文件外链异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "获取文件外链异常");
        }
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucketName
     * @param objectName 文件名称
     * @param expires 过期时间 <=7天
     * @param timeUnit 时间单位
     * @return String
     */
    public String getObjectUrl(String bucketName, String objectName, Integer expires, TimeUnit timeUnit) {
        try {
            if(Objects.isNull(expires)){
                expires = 24;
                timeUnit = TimeUnit.HOURS;
            }
            GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs.builder()
                 .method(Method.GET)
                 .bucket(bucketName)
                 .object(objectName)
                 .expiry(expires, timeUnit)
                 .build();
            return client.getPresignedObjectUrl(urlArgs);
        } catch (Exception e) {
            log.error("获取文件外链异常，{}", e.getMessage(), e);
            throw new BizException(BizErrCode.FAIL, "获取文件外链异常");
        }
    }
}
