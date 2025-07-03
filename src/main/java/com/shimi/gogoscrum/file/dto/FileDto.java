package com.shimi.gogoscrum.file.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.model.FileType;
import com.shimi.gogoscrum.file.model.TargetType;
import com.shimi.gogoscrum.file.service.FileStorage;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class FileDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 8593484462784380618L;
    private String name;
    private String fullPath;
    private String urlPrefix;
    private String url;
    private FileType type;
    private TargetType targetType;
    private Long size;
    private FileStorage.FileStorageProvider storageProvider;
    private boolean folder;
    private Long projectId;
    private FileDto parent;

    @Override
    public File toEntity() {
        File entity = new File();
        BeanUtils.copyProperties(this, entity);

        if(this.parent != null) {
            entity.setParent(this.parent.toEntity());
        }

        return entity;
    }

    public FileDto() {
    }

    public FileDto(Long id) {
        this.id = id;
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

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public FileDto getParent() {
        return parent;
    }

    public void setParent(FileDto parent) {
        this.parent = parent;
    }

    public FileStorage.FileStorageProvider getStorageProvider() {
        return storageProvider;
    }

    public void setStorageProvider(FileStorage.FileStorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public FileDto normalize() {
        FileDto dto = new FileDto();
        dto.setId(id);
        dto.setUrl(url);
        dto.setType(type);
        return dto;
    }
}
