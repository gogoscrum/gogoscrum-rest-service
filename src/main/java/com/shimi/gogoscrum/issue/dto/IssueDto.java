package com.shimi.gogoscrum.issue.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.component.model.Component;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.issue.model.Comment;
import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.issue.model.IssueType;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.sprint.dto.SprintDto;
import com.shimi.gogoscrum.tag.dto.TagDto;
import com.shimi.gogoscrum.tag.model.Tag;
import com.shimi.gogoscrum.testing.dto.TestCaseDto;
import com.shimi.gogoscrum.testing.dto.TestPlanDto;
import com.shimi.gogoscrum.user.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class IssueDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 6375428419379918110L;

    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long code;
    private String description;
    private IssueType type;
    private Priority priority = Priority.NORMAL;
    private Integer seq = 0;
    private Float storyPoints;
    private String projectCode;
    private Long projectId;
    private Long componentId;
    private String componentName;
    private SprintDto sprint;
    private UserDto owner;
    private IssueGroupDto issueGroup;
    private List<CommentDto> comments;
    private Date completedTime;
    private Date dueTime;
    private Float estimatedHours;
    private Float actualHours;
    private TestCaseDto testCase;
    private TestPlanDto testPlan;
    private List<FileDto> files = new ArrayList<>();
    private List<IssueDto> linkToIssues = new ArrayList<>();
    private List<IssueDto> linkedByIssues = new ArrayList<>();
    private List<TagDto> tags = new ArrayList<>();

    private int commentsCount;
    private int filesCount;
    private int linksCount;
    private int linkedCount;

    @Override
    public Issue toEntity() {
        Issue entity = new Issue();
        BeanUtils.copyProperties(this, entity, "files", "comments", "linkToIssues", "linkedByIssues", "tags");

        if (this.sprint != null) {
            entity.setSprint(this.sprint.toEntity());
        }

        if (this.owner != null) {
            entity.setOwner(this.owner.toEntity());
        }

        if (this.issueGroup != null) {
            entity.setIssueGroup(this.issueGroup.toEntity());
        }

        if (this.projectId != null) {
            entity.setProject(new Project(this.projectId));
        }

        if (this.componentId != null) {
            entity.setComponent(new Component(this.componentId));
        }

        if (this.testCase != null) {
            entity.setTestCase(this.testCase.toEntity());
        }

        if (this.testPlan != null) {
            entity.setTestPlan(this.testPlan.toEntity());
        }

        if (!CollectionUtils.isEmpty(this.comments)){
            List<Comment> commentEntities = this.comments.stream().map(CommentDto::toEntity).collect(Collectors.toList());
            entity.setComments(commentEntities);
        }

        if (!CollectionUtils.isEmpty(this.linkToIssues)){
            List<Issue> linkToIssueEntities = this.linkToIssues.stream().map(IssueDto::toEntity).collect(Collectors.toList());
            entity.setLinkToIssues(linkToIssueEntities);
        }

        if (!CollectionUtils.isEmpty(this.linkedByIssues)){
            List<Issue> linkedByIssueEntities = this.linkedByIssues.stream().map(IssueDto::toEntity).collect(Collectors.toList());
            entity.setLinkedByIssues(linkedByIssueEntities);
        }

        if (!CollectionUtils.isEmpty(this.tags)){
            List<Tag> tagEntities = this.tags.stream().map(TagDto::toEntity).collect(Collectors.toList());
            entity.setTags(tagEntities);
        }

        return entity;
    }

    public IssueDto() {
    }

    public IssueDto(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.projectCode + "-" + code;
    }

    public Long getShortCode() {
        return code;
    }

    public void setCode(Long code) {
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

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public SprintDto getSprint() {
        return sprint;
    }

    public void setSprint(SprintDto sprint) {
        this.sprint = sprint;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

    public Date getDueTime() {
        return dueTime;
    }

    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    public List<FileDto> getFiles() {
        return files;
    }

    public void setFiles(List<FileDto> files) {
        this.files = files;
    }

    public IssueGroupDto getIssueGroup() {
        return issueGroup;
    }

    public void setIssueGroup(IssueGroupDto issueGroup) {
        this.issueGroup = issueGroup;
    }

    public List<IssueDto> getLinkToIssues() {
        return linkToIssues;
    }

    public void setLinkToIssues(List<IssueDto> linkToIssues) {
        this.linkToIssues = linkToIssues;
    }

    public List<IssueDto> getLinkedByIssues() {
        return linkedByIssues;
    }

    public void setLinkedByIssues(List<IssueDto> linkedByIssues) {
        this.linkedByIssues = linkedByIssues;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(int filesCount) {
        this.filesCount = filesCount;
    }

    public int getLinksCount() {
        return linksCount;
    }

    public void setLinksCount(int linksCount) {
        this.linksCount = linksCount;
    }

    public int getLinkedCount() {
        return linkedCount;
    }

    public void setLinkedCount(int linkedCount) {
        this.linkedCount = linkedCount;
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

    public TestCaseDto getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCaseDto testCase) {
        this.testCase = testCase;
    }

    public TestPlanDto getTestPlan() {
        return testPlan;
    }

    public void setTestPlan(TestPlanDto testPlan) {
        this.testPlan = testPlan;
    }
}
