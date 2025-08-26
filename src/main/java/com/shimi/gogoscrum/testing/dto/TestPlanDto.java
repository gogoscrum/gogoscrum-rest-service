package com.shimi.gogoscrum.testing.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.testing.model.TestPlan;
import com.shimi.gogoscrum.testing.model.TestType;
import com.shimi.gogoscrum.user.dto.UserDto;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.Date;

public class TestPlanDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = -9112235291686840738L;
    private Long projectId;
    private String name;
    private Date startDate;
    private Date endDate;
    private TestType type;
    private UserDto owner;
    private Long caseCount = 0L;
    private Long executedCount = 0L;
    private Long failedCount = 0L;
    private Long successCount = 0L;
    private Long blockedCount = 0L;
    private Long skippedCount = 0L;

    @Override
    public TestPlan toEntity() {
        TestPlan entity = new TestPlan();
        BeanUtils.copyProperties(this, entity);

        if (this.owner != null) {
            entity.setOwner(this.owner.toEntity());
        }

        return entity;
    }

    public TestPlanDto() {
    }

    public TestPlanDto(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public TestType getType() {
        return type;
    }

    public void setType(TestType type) {
        this.type = type;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public Long getCaseCount() {
        return caseCount;
    }

    public void setCaseCount(Long caseCount) {
        this.caseCount = caseCount;
    }

    public Long getExecutedCount() {
        return executedCount;
    }

    public void setExecutedCount(Long executedCount) {
        this.executedCount = executedCount;
    }

    public Long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Long failedCount) {
        this.failedCount = failedCount;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getBlockedCount() {
        return blockedCount;
    }

    public void setBlockedCount(Long blockedCount) {
        this.blockedCount = blockedCount;
    }

    public Long getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(Long skippedCount) {
        this.skippedCount = skippedCount;
    }

    /**
     * Calculate the progress of the test plan based on executed and total cases.
     * @return the progress percentage as an integer
     */
    public int getProgress() {
        long total = caseCount != null ? caseCount : 0L;
        long executed = executedCount != null ? executedCount : 0L;

        if (total == 0) {
            return 0; // Avoid division by zero
        }

        return Math.round((float) executed / total * 100);
    }

    /**
     * Normalize the DTO to remove any sensitive information, only keeping minimal fields.
     * @return the normalized TestPlanDTO
     */
    @Override
    public TestPlanDto normalize() {
        TestPlanDto normalized = new TestPlanDto();

        normalized.setId(this.id);
        normalized.setName(this.name);

        return normalized;
    }
}
