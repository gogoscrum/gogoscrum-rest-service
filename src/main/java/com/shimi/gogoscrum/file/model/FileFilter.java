package com.shimi.gogoscrum.file.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;
import java.util.List;

public class FileFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = -7042839464032343595L;
    private Long projectId;
    private Long creatorId;
    private List<Long> creators;
    private TargetType targetType;
    private List<TargetType> targetTypes;
    private List<FileType> fileTypes;
    private Long parentId;
    private boolean rootLevel;
    private String keyword;
    private Boolean folder;

    public FileFilter() {
        this.setOrders(List.of(new Order("folder", Direction.DESC), new Order("id", Direction.DESC)));
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean isRootLevel() {
        return rootLevel;
    }

    public void setRootLevel(boolean rootLevel) {
        this.rootLevel = rootLevel;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public List<TargetType> getTargetTypes() {
        return targetTypes;
    }

    public void setTargetTypes(List<TargetType> targetTypes) {
        this.targetTypes = targetTypes;
    }

    public List<FileType> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<FileType> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public Boolean getFolder() {
        return folder;
    }

    public void setFolder(Boolean folder) {
        this.folder = folder;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<Long> getCreators() {
        return creators;
    }

    public void setCreators(List<Long> creators) {
        this.creators = creators;
    }
}
