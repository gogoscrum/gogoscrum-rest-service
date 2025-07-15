package com.shimi.gogoscrum.file.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileUploadToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 3296083971513494298L;
    private String uploadActionUrl;
    private String provider;
    private String path;
    private String sourceFileName;
    private String targetFileName;
    private String urlPrefix;
    private Long projectId;
    private TargetType targetType;
    /**
     * The provider-specific parameters for the upload request.
     */
    private Map<String, String> params = new HashMap<>();

    public String getUploadActionUrl() {
        return uploadActionUrl;
    }

    public void setUploadActionUrl(String uploadActionUrl) {
        this.uploadActionUrl = uploadActionUrl;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
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

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileUploadToken{");
        sb.append("uploadActionUrl='").append(uploadActionUrl).append('\'');
        sb.append(", provider='").append(provider).append('\'');
        sb.append(", path='").append(path).append('\'');
        sb.append(", sourceFileName='").append(sourceFileName).append('\'');
        sb.append(", targetFileName='").append(targetFileName).append('\'');
        sb.append(", urlPrefix='").append(urlPrefix).append('\'');
        sb.append(", projectId=").append(projectId);
        sb.append(", targetType=").append(targetType);
        sb.append('}');
        return sb.toString();
    }
}
