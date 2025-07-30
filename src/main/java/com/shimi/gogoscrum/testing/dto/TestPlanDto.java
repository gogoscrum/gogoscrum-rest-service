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
}
