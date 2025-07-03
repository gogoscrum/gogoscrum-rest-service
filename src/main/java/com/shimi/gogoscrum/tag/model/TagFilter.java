package com.shimi.gogoscrum.tag.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;

public class TagFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = 4697000402793352578L;
    private Long projectId;
    private String keyword;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
