package com.shimi.gogoscrum.testing.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.testing.dto.TestPlanItemDto;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

@Entity
public class TestPlanItem extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -5125452752727206777L;
    private Long testPlanId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id")
    private TestCase testCase;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "latest_run_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private TestRun latestRun;

    @Override
    public TestPlanItemDto toDto() {
        return this.toDto(false);
    }

    @Override
    public TestPlanItemDto toDto(boolean detailed) {
        TestPlanItemDto dto = new TestPlanItemDto();
        BeanUtils.copyProperties(this, dto);

        if (this.testCase != null) {
            testCase.setLatestRun(null); // Unnecessary for front-end
            dto.setTestCase(this.testCase.toDto());
        }

        if (this.latestRun != null) {
            latestRun.setTestCase(null); // Avoid circular reference
            dto.setLatestRun(this.latestRun.toDto());
        }

        return dto;
    }

    public TestPlanItem() {
    }

    public TestPlanItem(Long id) {
        this.id = id;
    }

    public Long getTestPlanId() {
        return testPlanId;
    }

    public void setTestPlanId(Long testPlanId) {
        this.testPlanId = testPlanId;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public TestRun getLatestRun() {
        return latestRun;
    }

    public void setLatestRun(TestRun latestRun) {
        this.latestRun = latestRun;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestPlanItem{");
        sb.append("id=").append(id);
        sb.append(", testPlanId=").append(testPlanId);
        sb.append(", testCase=").append(testCase);
        sb.append('}');
        return sb.toString();
    }
}
