package com.shimi.gogoscrum.testing.model;

import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;
import java.util.List;

public class TestCaseFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = 4576831627489574138L;
    private Long projectId;
    private String keyword;
    private List<Long> componentIds;
    private List<TestType> types;
    private List<Priority> priorities;
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

    public List<Long> getComponentIds() {
        return componentIds;
    }

    public void setComponentIds(List<Long> componentIds) {
        this.componentIds = componentIds;
    }

    public List<TestType> getTypes() {
        return types;
    }

    public void setTypes(List<TestType> types) {
        this.types = types;
    }

    public List<Priority> getPriorities() {
        return priorities;
    }

    public void setPriorities(List<Priority> priorities) {
        this.priorities = priorities;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}