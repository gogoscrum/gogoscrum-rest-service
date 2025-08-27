package com.shimi.gogoscrum.sprint.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.history.model.Historical;
import com.shimi.gogoscrum.issue.dto.IssueDto;
import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.sprint.dto.SprintDto;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sprint")
public class Sprint extends BaseEntity implements Historical {
    @Serial
    private static final long serialVersionUID = -5837280105424829935L;
    private String name;
    private String goal;
    private Boolean backlog = Boolean.FALSE;
    private Date startDate;
    private Date endDate;
    private Long totalIssueCount = 0L;
    private Long doneIssueCount = 0L;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "sprint", fetch = FetchType.LAZY)
    @OrderBy("seq ASC, priority DESC, storyPoints ASC, id ASC")
    private List<Issue> issues = new ArrayList<>();

    @Override
    public SprintDto toDto() {
       return this.toDto(false);
    }

    @Override
    public SprintDto toDto(boolean detailed) {
        return this.toDto(detailed, detailed);
    }

    public SprintDto toDto(boolean createdBy, boolean detailed) {
        SprintDto dto = new SprintDto();
        BeanUtils.copyProperties(this, dto, "issues", "project");

        if(createdBy && this.createdBy != null) {
            dto.setCreatedBy(this.createdBy.toDto());
        }

        if (detailed) {
            if (!CollectionUtils.isEmpty(this.issues)) {
                List<IssueDto> issueDtos = this.issues.stream().map(Issue::toDtoForList).toList();
                dto.setIssues(issueDtos);
            }

            if (this.project != null) {
                dto.setProject(this.project.toDto());
            }
        }

        return dto;
    }

    public Sprint() {
    }

    public Sprint(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public Boolean getBacklog() {
        return backlog;
    }

    public void setBacklog(Boolean backlog) {
        this.backlog = backlog;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Float getProgress() {
        if(totalIssueCount != null && totalIssueCount > 0) {
            return (float)(doneIssueCount != null ? doneIssueCount : 0) / totalIssueCount;
        } else {
            return 0F;
        }
    }

    public Long getTotalIssueCount() {
        return totalIssueCount;
    }

    public void setTotalIssueCount(Long totalIssueCount) {
        this.totalIssueCount = totalIssueCount;
    }

    public Long getDoneIssueCount() {
        return doneIssueCount;
    }

    public void setDoneIssueCount(Long doneIssueCount) {
        this.doneIssueCount = doneIssueCount;
    }

    public boolean isActive() {
        Date now = new Date();
        return startDate != null && endDate != null && startDate.before(now) && endDate.after(now);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Sprint{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", projectId=").append(project.getId());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getDetails() {
        final StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", projectId=").append(project.getId());
        return sb.toString();
    }
}
