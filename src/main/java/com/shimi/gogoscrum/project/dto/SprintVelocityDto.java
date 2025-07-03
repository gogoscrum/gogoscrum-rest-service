package com.shimi.gogoscrum.project.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class SprintVelocityDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -7137636612026587599L;

    private Long sprintId;
    private String sprintName;
    private Long completedIssueCount;
    private Double completedStoryPoints;
    private Date startDate;
    private Date endDate;

    public SprintVelocityDto(Long sprintId, String sprintName, Date startDate, Date endDate, Long completedIssueCount,
                             Double completedStoryPoints) {
        this.sprintId = sprintId;
        this.sprintName = sprintName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.completedIssueCount = completedIssueCount;
        this.completedStoryPoints = completedStoryPoints;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public Long getCompletedIssueCount() {
        return completedIssueCount;
    }

    public void setCompletedIssueCount(Long completedIssueCount) {
        this.completedIssueCount = completedIssueCount;
    }

    public Double getCompletedStoryPoints() {
        return completedStoryPoints;
    }

    public void setCompletedStoryPoints(Double completedStoryPoints) {
        this.completedStoryPoints = completedStoryPoints;
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
}
