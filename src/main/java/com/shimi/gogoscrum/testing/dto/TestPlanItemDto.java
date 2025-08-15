package com.shimi.gogoscrum.testing.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.testing.model.TestPlanItem;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class TestPlanItemDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = -2278030931158490073L;
    private Long testPlanId;
    private TestCaseDto testCase;
    private TestRunDto latestRun;

    @Override
    public TestPlanItem toEntity() {
        TestPlanItem entity = new TestPlanItem();
        BeanUtils.copyProperties(this, entity);
        if (this.testCase != null) {
            entity.setTestCase(this.testCase.toEntity());
        }

        return entity;
    }

    public Long getTestPlanId() {
        return testPlanId;
    }

    public void setTestPlanId(Long testPlanId) {
        this.testPlanId = testPlanId;
    }

    public TestCaseDto getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCaseDto testCase) {
        this.testCase = testCase;
    }

    public TestRunDto getLatestRun() {
        return latestRun;
    }

    public void setLatestRun(TestRunDto latestRun) {
        this.latestRun = latestRun;
    }
}
