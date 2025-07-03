package com.shimi.gogoscrum.file.model;

import com.shimi.gogoscrum.file.service.FileStorage;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class FileUploadToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 3296083971513494298L;
    private String uploadActionUrl;
    private FileStorage.FileStorageProvider provider;
    private String path;
    private String sourceFileName;
    private String targetFileName;
    private String urlPrefix;
    private Long projectId;
    private TargetType targetType;

    public String getUploadActionUrl() {
        return uploadActionUrl;
    }

    public void setUploadActionUrl(String uploadActionUrl) {
        this.uploadActionUrl = uploadActionUrl;
    }

    public FileStorage.FileStorageProvider getProvider() {
        return provider;
    }

    public void setProvider(FileStorage.FileStorageProvider provider) {
        this.provider = provider;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public String getTargetFileUrl() {
        return Objects.requireNonNullElse(urlPrefix, "/") + path + "/" + targetFileName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileUploadToken{");
        sb.append("action='").append(uploadActionUrl).append('\'');
        sb.append(", provider='").append(provider).append('\'');
        sb.append(", path='").append(path).append('\'');
        sb.append(", filename='").append(targetFileName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
