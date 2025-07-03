package com.shimi.gogoscrum.doc.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;

public class DocFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = 261537799353622749L;
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
