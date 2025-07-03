package com.shimi.gogoscrum.project.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.issue.dto.IssueGroupDto;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.sprint.dto.SprintDto;
import com.shimi.gogoscrum.user.dto.UserDto;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 8062839279046369071L;

    private String name;
    private String code;
    private Date startDate;
    private Date endDate;
    private List<SprintDto> sprints = new ArrayList<>();
    private boolean archived = false;
    private List<IssueGroupDto> issueGroups = new ArrayList<>();
    private List<ProjectMemberDto> projectMembers = new ArrayList<>();
    private UserDto owner;
    private FileDto avatar;
    private String description;
    private Boolean timeTrackingEnabled = false;
    private Long fileCount = 0L;
    private Long totalFileSize = 0L;

    @Override
    public Project toEntity() {
        Project entity = new Project();
        BeanUtils.copyProperties(this, entity, "sprints", "users", "issueGroups", "projectMembers");
        return entity;
    }

    public ProjectDto() {
    }

    public ProjectDto(Long id) {
        this.id = id;
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

    public List<SprintDto> getSprints() {
        return sprints;
    }

    public void setSprints(List<SprintDto> sprints) {
        this.sprints = sprints;
    }

    public List<IssueGroupDto> getIssueGroups() {
        return issueGroups;
    }

    public void setIssueGroups(List<IssueGroupDto> issueGroups) {
        this.issueGroups = issueGroups;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<ProjectMemberDto> getProjectMembers() {
        return projectMembers;
    }

    public void setProjectMembers(List<ProjectMemberDto> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public FileDto getAvatar() {
        return avatar;
    }

    public void setAvatar(FileDto avatar) {
        this.avatar = avatar;
    }

    public String getAvatarUrl() {
        return avatar != null ? avatar.getUrl() : null;
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
}
