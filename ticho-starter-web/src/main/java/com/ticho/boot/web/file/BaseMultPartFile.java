package com.ticho.boot.web.file;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义 MultipartFile
 * <p>用于文件流转MultipartFile</p>
 *
 * @author zhajianjun
 * @date 2022-07-12 0:28
 */
public class BaseMultPartFile implements MultipartFile {
    private final String name;
    private final String originalFilename;
    @Nullable
    private final String contentType;
    private final byte[] content;

    public BaseMultPartFile(String name, @Nullable byte[] content) {
        this(name, "", null, content);
    }

    public BaseMultPartFile(String name, InputStream contentStream) throws IOException {
        this(name, "", null, FileCopyUtils.copyToByteArray(contentStream));
    }

    public BaseMultPartFile(String name, @Nullable String originalFilename, @Nullable String contentType,
            @Nullable byte[] content) {
        Assert.hasLength(name, "Name must not be empty");
        this.name = name;
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.contentType = contentType;
        this.content = content != null ? content : new byte[0];
    }

    public BaseMultPartFile(String name, @Nullable String originalFilename, @Nullable String contentType,
            InputStream contentStream) throws IOException {
        this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
    }

    public BaseMultPartFile(@NonNull MultipartFile multipartFile) throws IOException {
        this(multipartFile.getName(), multipartFile.getOriginalFilename(), multipartFile.getContentType(),
                multipartFile.getBytes());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @NonNull
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.content.length == 0;
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() {
        return this.content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }
}
