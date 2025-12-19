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
    private List<TestRun.TestRunStatus> runStatuses;
    private List<Long> owners;
    private List<Long> creators;
    private Boolean deleted = Boolean.FALSE;
    // for exporting, now supports "en", "cn"
    private String language;

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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<TestRun.TestRunStatus> getRunStatuses() {
        return runStatuses;
    }

    public void setRunStatuses(List<TestRun.TestRunStatus> runStatuses) {
        this.runStatuses = runStatuses;
    }
}