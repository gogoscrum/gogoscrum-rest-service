package com.shimi.gogoscrum.testing.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;

public class TestPlanFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = -8999639774002706528L;
    private Long projectId;
    private String keyword;
    private Boolean deleted = Boolean.FALSE;

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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}