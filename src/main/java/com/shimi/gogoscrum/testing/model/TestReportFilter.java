package com.shimi.gogoscrum.testing.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;
import java.util.List;

public class TestReportFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = 40164471484106146L;
    private Long projectId;
    private Long planId;
    private String keyword;
    private List<Long> planIds;
    private List<Long> creators;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Long> getPlanIds() {
        return planIds;
    }

    public void setPlanIds(List<Long> planIds) {
        this.planIds = planIds;
    }

    public List<Long> getCreators() {
        return creators;
    }

    public void setCreators(List<Long> creators) {
        this.creators = creators;
    }
}