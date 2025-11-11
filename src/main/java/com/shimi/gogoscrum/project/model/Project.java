package com.shimi.gogoscrum.project.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.history.model.Historical;
import com.shimi.gogoscrum.issue.dto.IssueGroupDto;
import com.shimi.gogoscrum.issue.model.IssueGroup;
import com.shimi.gogoscrum.issue.model.IssueGroupStatus;
import com.shimi.gogoscrum.project.dto.ProjectDto;
import com.shimi.gogoscrum.project.dto.ProjectMemberDto;
import com.shimi.gogoscrum.sprint.dto.SprintDto;
import com.shimi.gogoscrum.sprint.model.Sprint;
import com.shimi.gogoscrum.user.model.User;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "project")
public class Project extends BaseEntity implements Historical {
    @Serial
    private static final long serialVersionUID = -1160435223851775784L;
    private String name;
    private String code;
    private Long lastIssueSeq;
    private Date startDate;
    private Date endDate;
    private boolean deleted = false;
    private boolean archived = false;
    @OneToOne
    @JoinColumn(name = "avatar_file_id")
    private File avatar;
    private String description;
    private Boolean timeTrackingEnabled = false;
    private Long fileCount = 0L;
    private Long totalFileSize = 0L;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project", fetch = FetchType.LAZY)
    @OrderBy("startDate DESC, id DESC")
    private List<Sprint> sprints = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project", fetch = FetchType.LAZY)
    @OrderBy("seq ASC")
    private List<IssueGroup> issueGroups = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    public Project(Long projectId) {
        this.id = projectId;
    }

    public Project() {

    }

    @Override
    public ProjectDto toDto() {
        return this.toDto(false);
    }

    @Override
    public ProjectDto toDto(boolean detailed) {
        ProjectDto dto = new ProjectDto();
        BeanUtils.copyProperties(this, dto, "sprints", "users", "owner", "issueGroups", "projectMembers");

        if (this.getOwner() != null) {
            dto.setOwner(this.getOwner().toDto().normalize());
        }

        if (this.avatar != null) {
            dto.setAvatar(this.avatar.toDto().normalize());
        }

        if (detailed) {
            if (!CollectionUtils.isEmpty(this.projectMembers)) {
                List<ProjectMemberDto> projectMemberDtos = this.projectMembers.stream().map(ProjectMember::toDto).toList();
                dto.setProjectMembers(projectMemberDtos);
            }

            if (!CollectionUtils.isEmpty(this.sprints)) {
                List<SprintDto> sprintDtos = this.sprints.stream().map(Sprint::toDto).toList();
                dto.setSprints(sprintDtos);
            }

            if (!CollectionUtils.isEmpty(this.issueGroups)) {
                List<IssueGroupDto> issueGroupDtos = this.issueGroups.stream().map(IssueGroup::toDto).toList();
                dto.setIssueGroups(issueGroupDtos);
            }
        }
        return dto;
    }

    public User getOwner() {
        Optional<ProjectMember> owner = this.getProjectMembers().stream()
                .filter(participator -> ProjectMemberRole.OWNER.equals(participator.getRole())).findFirst();

        return owner.map(ProjectMember::getUser).orElse(null);
    }

    public ProjectMember getMemberByUserId(Long userId) {
        Optional<ProjectMember> member = this.getProjectMembers().stream()
                .filter(participator -> participator.getUser().getId().equals(userId)).findFirst();

        return member.orElse(null);
    }

    public ProjectMember getOwnerMember() {
        Optional<ProjectMember> member = this.getProjectMembers().stream()
                .filter(participator -> ProjectMemberRole.OWNER.equals(participator.getRole())).findFirst();

        return member.orElse(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getLastIssueSeq() {
        return lastIssueSeq;
    }

    public void setLastIssueSeq(Long lastIssueSeq) {
        this.lastIssueSeq = lastIssueSeq;
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

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public Sprint getBacklogSprint() {
        if (!CollectionUtils.isEmpty(this.sprints)) {
            return this.sprints.stream().filter(Sprint::getBacklog).findFirst().orElse(null);
        } else {
            return null;
        }
    }

    public List<IssueGroup> getIssueGroups() {
        return issueGroups;
    }

    public void setIssueGroups(List<IssueGroup> issueGroups) {
        this.issueGroups = issueGroups;
    }

    public IssueGroup getToDoIssueGroup() {
        return this.issueGroups.stream().filter(group -> IssueGroupStatus.TO_DO.equals(group.getStatus())).findFirst()
                .orElse(null);
    }

    public List<ProjectMember> getProjectMembers() {
        return projectMembers;
    }

    public void setProjectMembers(List<ProjectMember> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public File getAvatar() {
        return avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getTimeTrackingEnabled() {
        return timeTrackingEnabled;
    }

    public void setTimeTrackingEnabled(Boolean timeTrackingEnabled) {
        this.timeTrackingEnabled = timeTrackingEnabled;
    }

    public Long getFileCount() {
        return fileCount;
    }

    public void setFileCount(Long fileCount) {
        this.fileCount = fileCount;
    }

    public Long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(Long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Project{");
        sb.append("id=").append(id);
        sb.append(", code='").append(code).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getDetails() {
        return "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code;
    }
}