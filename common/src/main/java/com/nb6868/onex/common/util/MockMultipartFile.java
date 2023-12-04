package com.nb6868.onex.common.util;

import jakarta.annotation.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * MockMultipartFile,参考spring-test中的方法
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class MockMultipartFile implements MultipartFile {

    private final String name;
    private final String originalFilename;

    @Nullable
    private final String contentType;
    private final byte[] content;

    public MockMultipartFile(String name, @Nullable byte[] content) {
        this(name, "", (String)null, (byte[])content);
    }

    public MockMultipartFile(String name, InputStream contentStream) throws IOException {
        this(name, "", (String)null, (byte[])FileCopyUtils.copyToByteArray(contentStream));
    }

    public MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content) {
        this.name = name;
        this.originalFilename = originalFilename != null ? originalFilename : "";
        this.contentType = contentType;
        this.content = content != null ? content : new byte[0];
    }

    public MockMultipartFile(String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream) throws IOException {
        this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Nullable
    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.content.length == 0;
    }

    @Override
    public long getSize() {
        return (long)this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }

}
