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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TestPlan{");
        sb.append("id=").append(id);
        sb.append(", projectId=").append(projectId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", startDate=").append(startDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", deleted=").append(deleted);
        sb.append('}');
        return sb.toString();
    }
}
