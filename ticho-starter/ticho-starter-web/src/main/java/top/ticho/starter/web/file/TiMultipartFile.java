package top.ticho.starter.web.file;

import lombok.NonNull;
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
public class TiMultipartFile implements MultipartFile {
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    public TiMultipartFile(String name, byte[] content) {
        this(name, "", null, content);
    }

    public TiMultipartFile(String name, InputStream contentStream) throws IOException {
        this(name, "", null, FileCopyUtils.copyToByteArray(contentStream));
    }

    public TiMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
        Assert.hasLength(name, "Name must not be empty");
        this.name = name;
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.contentType = contentType;
        this.content = content != null ? content : new byte[0];
    }

    public TiMultipartFile(String name, String originalFilename, String contentType, InputStream contentStream) throws IOException {
        this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
    }

    public TiMultipartFile(@NonNull MultipartFile file) throws IOException {
        this(file.getName(), file.getOriginalFilename(), file.getContentType(), file.getBytes());
    }

    @NonNull
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
    @NonNull
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
    public byte @NonNull [] getBytes() {
        return this.content;
    }

    @NonNull
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }

}
