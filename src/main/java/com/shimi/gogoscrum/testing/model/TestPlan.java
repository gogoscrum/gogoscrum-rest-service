package com.shimi.gogoscrum.testing.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.testing.dto.TestPlanDto;
import com.shimi.gogoscrum.user.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.Date;

@Entity
public class TestPlan extends BaseEntity {
    @Serial
    private static final long serialVersionUID = -6161570762726533308L;
    private Long projectId;
    private String name;
    private Date startDate;
    private Date endDate;
    @Enumerated(EnumType.STRING)
    private TestType type;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private User owner;
    private Long caseCount = 0L;
    private Long executedCount = 0L;
    private Long failedCount = 0L;
    private Long successCount = 0L;
    private Long blockedCount = 0L;
    private Long skippedCount = 0L;
    private boolean deleted = false;

    @Override
    public TestPlanDto toDto() {
        return this.toDto(false);
    }

    @Override
    public TestPlanDto toDto(boolean detailed) {
        TestPlanDto dto = new TestPlanDto();
        BeanUtils.copyProperties(this, dto);

        if (this.createdBy != null) {
            dto.setCreatedBy(this.createdBy.toDto());
        }

        if (this.owner != null) {
            dto.setOwner(this.owner.toDto().normalize());
        }

        return dto;
    }

    public TestPlan() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestPlan(Long id) {
        this.id = id;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public TestType getType() {
        return type;
    }

    public void setType(TestType type) {
        this.type = type;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
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

    public Long getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(Long skippedCount) {
        this.skippedCount = skippedCount;
    }

    public Long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Long failedCount) {
        this.failedCount = failedCount;
    }

    public Long getBlockedCount() {
        return blockedCount;
    }

    public void setBlockedCount(Long blockedCount) {
        this.blockedCount = blockedCount;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestPlan{");
        sb.append("projectId=").append(projectId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", caseCount=").append(caseCount);
        sb.append(", executedCount=").append(executedCount);
        sb.append(", failedCount=").append(failedCount);
        sb.append(", successCount=").append(successCount);
        sb.append(", blockedCount=").append(blockedCount);
        sb.append(", skippedCount=").append(skippedCount);
        sb.append(", deleted=").append(deleted);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
