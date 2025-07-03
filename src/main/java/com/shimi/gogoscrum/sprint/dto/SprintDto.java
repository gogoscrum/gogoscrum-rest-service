package com.shimi.gogoscrum.sprint.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.issue.dto.IssueDto;
import com.shimi.gogoscrum.project.dto.ProjectDto;
import com.shimi.gogoscrum.sprint.model.Sprint;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SprintDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 6641652079566435930L;
    private String name;
    private String goal;
    private Boolean backlog = Boolean.FALSE;
    private Date startDate;
    private Date endDate;
    private ProjectDto project;
    private List<IssueDto> issues = new ArrayList<>();
    private Float progress;
    private long totalIssueCount;
    private long doneIssueCount;

    @Override
    public Sprint toEntity() {
        Sprint entity = new Sprint();
        BeanUtils.copyProperties(this, entity, "issues", "project");

        if (this.project != null) {
            entity.setProject(this.project.toEntity());
        }

        return entity;
    }

    public SprintDto() {
    }

    public SprintDto(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getBacklog() {
        return backlog;
    }

    public void setBacklog(Boolean isBackLog) {
        this.backlog = isBackLog;
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

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public List<IssueDto> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueDto> issues) {
        this.issues = issues;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Float getProgress() {
        return progress;
    }

    public void setProgress(Float progress) {
        this.progress = progress;
    }

    public long getTotalIssueCount() {
        return totalIssueCount;
    }

    public void setTotalIssueCount(long totalIssueCount) {
        this.totalIssueCount = totalIssueCount;
    }

    public long getDoneIssueCount() {
        return doneIssueCount;
    }

    public void setDoneIssueCount(long doneIssueCount) {
        this.doneIssueCount = doneIssueCount;
    }
}
