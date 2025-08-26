package com.shimi.gogoscrum.testing.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;
import java.util.List;

public class TestRunFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = -2013295293395171124L;
    private Long projectId;
    private String keyword;
    private Long caseId;
    private Long caseDetailsId;
    private Integer version;
    private Long planId;
    private TestRun.TestRunStatus status;
    private List<Long> planIds;
    private List<Long> creators;
    private List<TestRun.TestRunStatus> statuses;

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

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getCaseDetailsId() {
        return caseDetailsId;
    }

    public void setCaseDetailsId(Long caseDetailsId) {
        this.caseDetailsId = caseDetailsId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public TestRun.TestRunStatus getStatus() {
        return status;
    }

    public void setStatus(TestRun.TestRunStatus status) {
        this.status = status;
    }

    public List<Long> getCreators() {
        return creators;
    }

    public void setCreators(List<Long> creators) {
        this.creators = creators;
    }

    public List<Long> getPlanIds() {
        return planIds;
    }

    public void setPlanIds(List<Long> planIds) {
        this.planIds = planIds;
    }

    public List<TestRun.TestRunStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<TestRun.TestRunStatus> statuses) {
        this.statuses = statuses;
    }
}