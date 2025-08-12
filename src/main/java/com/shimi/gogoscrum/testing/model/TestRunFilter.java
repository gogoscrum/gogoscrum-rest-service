package com.shimi.gogoscrum.testing.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;
import java.util.List;

public class TestRunFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = -2013295293395171124L;
    private Long projectId;
    private Long testCaseId;
    private Long testCaseDetailsId;
    private Integer version;
    private Long testPlanId;
    private TestRun.TestRunStatus status;
    private List<Long> creators;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public Long getTestCaseDetailsId() {
        return testCaseDetailsId;
    }

    public void setTestCaseDetailsId(Long testCaseDetailsId) {
        this.testCaseDetailsId = testCaseDetailsId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getTestPlanId() {
        return testPlanId;
    }

    public void setTestPlanId(Long testPlanId) {
        this.testPlanId = testPlanId;
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
}