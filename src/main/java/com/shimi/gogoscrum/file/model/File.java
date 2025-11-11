package com.shimi.gogoscrum.file.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.file.dto.FileDto;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.Objects;

@Entity
public class File extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -4096920558859114260L;
    private String name;
    private String fullPath;
    private String urlPrefix;
    @Enumerated(EnumType.STRING)
    private FileType type;
    @Enumerated(EnumType.STRING)
    private TargetType targetType;
    private Long size;
    private String storageProvider;
    private Boolean folder = Boolean.FALSE;
    private Long projectId;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private File parent;

    @Override
    public FileDto toDto() {
        return this.toDto(false);
    }

    @Override
    public FileDto toDto(boolean detailed) {
        return this.toDto(detailed, 1);
    }

    public FileDto toDto(boolean detailed, int ancestorLevel) {
        FileDto dto = new FileDto();
        BeanUtils.copyProperties(this, dto);

        if(this.createdBy != null) {
            dto.setCreatedBy(this.createdBy.toDto().normalize());
        }

        if (this.parent != null && ancestorLevel > 0) {
            dto.setParent(this.parent.toDto(detailed, ancestorLevel - 1));
        }

        return dto;
    }

    public File() {
    }

    public File(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean getFolder() {
        return folder;
    }

    public void setFolder(Boolean folder) {
        this.folder = folder;
    }

    public File getParent() {
        return parent;
    }

    public void setParent(File parent) {
        this.parent = parent;
    }

    public String getStorageProvider() {
        return storageProvider;
    }

    public void setStorageProvider(String storageProvider) {
        this.storageProvider = storageProvider;
    }

    public String getUrl() {
        if (fullPath == null || fullPath.isEmpty()) {
            return null;
        } else if (fullPath.startsWith("http://") || fullPath.startsWith("https://")) {
            return fullPath;
        } else {
            return Objects.requireNonNullElse(urlPrefix, "/") + fullPath;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("File{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", folder=").append(folder);
        sb.append(", type=").append(type);
        sb.append(", size=").append(size);
        sb.append(", projectId=").append(projectId);
        sb.append(", targetType=").append(targetType);
        sb.append(", path='").append(fullPath).append('\'');
        sb.append(", urlPrefix='").append(urlPrefix).append('\'');
        sb.append(", storage=").append(storageProvider);
        sb.append('}');
        return sb.toString();
    }
}
