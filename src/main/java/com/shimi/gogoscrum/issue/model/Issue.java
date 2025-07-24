package com.shimi.gogoscrum.issue.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.component.model.Component;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.history.model.Historical;
import com.shimi.gogoscrum.issue.dto.CommentDto;
import com.shimi.gogoscrum.issue.dto.IssueDto;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.sprint.model.Sprint;
import com.shimi.gogoscrum.tag.dto.TagDto;
import com.shimi.gogoscrum.tag.model.Tag;
import com.shimi.gogoscrum.user.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Issue extends BaseEntity implements Historical {
    @Serial
    private static final long serialVersionUID = 2633981347091498557L;

    private String name;
    private String code;
    private String description;
    @Enumerated(EnumType.STRING)
    private IssueType type;
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;
    private Integer seq = 0;
    private Float storyPoints;
    private Date completedTime;
    private Date dueTime;
    private Float estimatedHours;
    private Float actualHours;

    @ManyToOne
    @JoinColumn(name = "component_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Component component;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Sprint sprint;

    @ManyToOne
    @JoinColumn(name = "issue_group_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private IssueGroup issueGroup;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private User owner;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "issue", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "issue", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private List<File> files = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_link",
            joinColumns = @JoinColumn(name = "issue_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "linked_issue_id", referencedColumnName = "id"))
    private List<Issue> linkToIssues = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_link",
            joinColumns = @JoinColumn(name = "linked_issue_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id", referencedColumnName = "id"))
    private List<Issue> linkedByIssues = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "issue_tag",
            joinColumns = @JoinColumn(name = "issue_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private List<Tag> tags = new ArrayList<>();

    public Issue() {
    }

    public Issue(Long id) {
        this.id = id;
    }

    @Override
    public IssueDto toDto() {
        return this.toDto(false);
    }

    public IssueDto toDtoForList() {
        IssueDto dto = new IssueDto();
        BeanUtils.copyProperties(this, dto, "comments", "owner", "sprint", "files", "linkToIssues", "linkedByIssues", "tags");

        if (this.project != null) {
            dto.setProjectCode(this.project.getCode());
            dto.setProjectId(this.project.getId());
        }

        if (this.component != null) {
            dto.setComponentName(this.component.getName());
            dto.setComponentId(this.component.getId());
        }

        if (this.owner != null) {
            dto.setOwner(this.owner.toDto());
        }

        if (this.sprint != null) {
            dto.setSprint(this.sprint.toDto());
        }

        if (this.issueGroup != null) {
            dto.setIssueGroup(this.issueGroup.toDto());
        }

        dto.setCommentsCount(this.comments.size());
        dto.setFilesCount(this.files.size());
        dto.setLinksCount(this.linkToIssues.size());
        dto.setLinkedCount(this.linkedByIssues.size());

        if (!CollectionUtils.isEmpty(this.tags)) {
            List<TagDto> tagDtos = this.tags.stream().map(Tag::toDto).toList();
            dto.setTags(tagDtos);
        }

        return dto;
    }

    @Override
    public IssueDto toDto(boolean detailed) {
        IssueDto dto = this.toDtoForList();

        if (detailed) {
            if (this.createdBy != null) {
                dto.setCreatedBy(this.createdBy.toDto());
            }

            if (!CollectionUtils.isEmpty(this.comments)) {
                List<CommentDto> commentDtos = this.comments.stream().map(Comment::toDto).toList();
                dto.setComments(commentDtos);
            }

            if (!CollectionUtils.isEmpty(this.files)) {
                List<FileDto> fileDtos = this.files.stream().map(File::toDto).toList();
                dto.setFiles(fileDtos);
            }

            if (!CollectionUtils.isEmpty(this.linkToIssues)) {
                List<IssueDto> linkToIssueDtos = this.linkToIssues.stream().map(Issue::toDto).toList();
                dto.setLinkToIssues(linkToIssueDtos);
            }

            if (!CollectionUtils.isEmpty(this.linkedByIssues)) {
                List<IssueDto> linkedByIssueDtos = this.linkedByIssues.stream().map(Issue::toDto).toList();
                dto.setLinkedByIssues(linkedByIssueDtos);
            }

            if (!CollectionUtils.isEmpty(this.tags)) {
                List<TagDto> tagDtos = this.tags.stream().map(Tag::toDto).toList();
                dto.setTags(tagDtos);
            }
        }

        return dto;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Float getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Float storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> commentList) {
        this.comments = commentList;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public IssueGroup getIssueGroup() {
        return issueGroup;
    }

    public void setIssueGroup(IssueGroup issueGroup) {
        this.issueGroup = issueGroup;
    }

    public List<Issue> getLinkToIssues() {
        return linkToIssues;
    }

    public void setLinkToIssues(List<Issue> linkToIssues) {
        this.linkToIssues = linkToIssues;
    }

    public List<Issue> getLinkedByIssues() {
        return linkedByIssues;
    }

    public void setLinkedByIssues(List<Issue> linkedByIssues) {
        this.linkedByIssues = linkedByIssues;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public boolean isDone() {
        return this.issueGroup != null && IssueGroupStatus.DONE.equals(issueGroup.getStatus());
    }

    public Float getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Float estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Float getActualHours() {
        return actualHours;
    }

    public void setActualHours(Float actualHours) {
        this.actualHours = actualHours;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Issue{");
        sb.append("name='").append(name).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", type=").append(type);
        sb.append(", priority=").append(priority);
        sb.append(", seq=").append(seq);
        sb.append(", storyPoints=").append(storyPoints);
        sb.append(", completedTime=").append(completedTime);
        sb.append(", dueTime=").append(dueTime);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public String getDetails() {
        final StringBuilder sb = new StringBuilder();
        sb.append("name='").append(name).append('\'');
        if (StringUtils.hasText(description)) {
            sb.append(", desc='").append(description).append('\'');
        }
        sb.append(", type=").append(type);
        sb.append(", priority=").append(priority);
        if (owner != null) {
            sb.append(", owner='").append(owner.getNickname()).append('\'');
        }
        if (issueGroup != null) {
            sb.append(", issueGroup='").append(issueGroup.getLabel()).append('\'');
        }
        if (sprint != null) {
            sb.append(", sprint='").append(sprint.getName()).append('\'');
        }
        if (storyPoints != null) {
            sb.append(", storyPoint=").append(storyPoints);
        }
        if (estimatedHours != null) {
            sb.append(", estimatedHours=").append(estimatedHours);
        }
        if (actualHours != null) {
            sb.append(", actualHours=").append(actualHours);
        }
        if (component != null) {
            sb.append(", component='").append(component.getName()).append('\'');
        }
        if (dueTime != null) {
            sb.append(", dueTime=").append(dueTime);
        }
        if (!CollectionUtils.isEmpty(files)) {
            sb.append(", files=").append(files.size());
        }
        if (!CollectionUtils.isEmpty(tags)) {
            sb.append(", tags=").append(tags.size());
        }
        if (!CollectionUtils.isEmpty(linkToIssues)) {
            sb.append(", linksTo=").append(linkToIssues.size());
        }
        if (!CollectionUtils.isEmpty(linkedByIssues)) {
            sb.append(", linkedBy=").append(linkedByIssues.size());
        }
        if (!CollectionUtils.isEmpty(comments)) {
            sb.append(", comments=").append(comments.size());
        }
        return sb.toString();
    }
}
