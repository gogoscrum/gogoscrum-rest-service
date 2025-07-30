package com.shimi.gogoscrum.testing.model;

import com.shimi.gsf.core.model.BaseFilter;

import java.io.Serial;

public class TestPlanItemFilter extends BaseFilter {
    @Serial
    private static final long serialVersionUID = 1613402174798480367L;
    private Long testPlanId;
    private String keyword;

    public Long getTestPlanId() {
        return testPlanId;
    }

    public void setTestPlanId(Long testPlanId) {
        this.testPlanId = testPlanId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}