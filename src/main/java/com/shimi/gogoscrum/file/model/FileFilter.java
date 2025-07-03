package com.shimi.gogoscrum.file.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;
import java.util.List;

public class FileFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = -7042839464032343595L;
    private Long projectId;
    private TargetType targetType;
    private Long parentId;
    private boolean rootLevel;
    private String keyword;

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
}
