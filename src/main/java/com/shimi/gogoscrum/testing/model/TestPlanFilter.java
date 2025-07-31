package com.shimi.gogoscrum.testing.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;
import java.util.List;

public class TestPlanFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = -8999639774002706528L;
    private Long projectId;
    private String keyword;
    private List<TestType> types;
    private List<Long> owners;
    private List<Long> creators;
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

    public List<TestType> getTypes() {
        return types;
    }

    public void setTypes(List<TestType> types) {
        this.types = types;
    }

    public List<Long> getOwners() {
        return owners;
    }

    public void setOwners(List<Long> owners) {
        this.owners = owners;
    }

    public List<Long> getCreators() {
        return creators;
    }

    public void setCreators(List<Long> creators) {
        this.creators = creators;
    }
}