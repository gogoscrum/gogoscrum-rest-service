package com.shimi.gogoscrum.testing.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gsf.core.dto.Dto;
import jakarta.persistence.Entity;

import java.io.Serial;

@Entity
public class TestCaseIssueLink extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 598046749855011879L;
    private Long testCaseId;
    private Long testPlanId;
    private Long issueId;

    @Override
    public Dto toDto() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public TestCaseIssueLink() {
    }

    public TestCaseIssueLink(Long id) {
        this.id = id;
    }

    public Long getTestPlanId() {
        return testPlanId;
    }

    public void setTestPlanId(Long testPlanId) {
        this.testPlanId = testPlanId;
    }

}
