package com.shimi.gogoscrum.testing.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.testing.model.TestReport;
import com.shimi.gogoscrum.user.dto.UserDto;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.Date;

public class TestReportDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = -1913059909038805612L;
    private Long projectId;
    private TestPlanDto testPlan;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private UserDto owner;
    private TestReport.CaseSummary caseSummary;
    private TestReport.BugSummary bugSummary;

    @Override
    public TestReport toEntity() {
        TestReport entity = new TestReport();
        BeanUtils.copyProperties(this, entity);

        if (this.testPlan != null) {
            entity.setTestPlan(testPlan.toEntity());
        }

        if (this.owner != null) {
            entity.setOwner(this.owner.toEntity());
        }

        return entity;
    }

    public TestReportDto() {
    }

    public TestReportDto(Long id) {
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

    public TestPlanDto getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(TestPlanDto testPlan) {
        this.testPlan = testPlan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public TestReport.CaseSummary getCaseSummary() {
        return caseSummary;
    }

    public void setCaseSummary(TestReport.CaseSummary caseSummary) {
        this.caseSummary = caseSummary;
    }

    public TestReport.BugSummary getBugSummary() {
        return bugSummary;
    }

    public void setBugSummary(TestReport.BugSummary bugSummary) {
        this.bugSummary = bugSummary;
    }
}
