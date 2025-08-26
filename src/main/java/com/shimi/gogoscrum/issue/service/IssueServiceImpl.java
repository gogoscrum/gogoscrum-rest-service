package com.shimi.gogoscrum.issue.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.common.util.Exporter;
import com.shimi.gogoscrum.component.model.Component;
import com.shimi.gogoscrum.component.service.ComponentService;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.service.FileService;
import com.shimi.gogoscrum.issue.dto.IssueCountDto;
import com.shimi.gogoscrum.issue.event.IssueSeqUpdatedEvent;
import com.shimi.gogoscrum.issue.model.*;
import com.shimi.gogoscrum.issue.repository.IssueRepository;
import com.shimi.gogoscrum.issue.repository.IssueSpecs;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.sprint.model.Sprint;
import com.shimi.gogoscrum.sprint.service.SprintService;
import com.shimi.gogoscrum.tag.model.Tag;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gogoscrum.user.service.UserService;
import com.shimi.gsf.core.event.EntityChangeEvent;
import com.shimi.gsf.core.exception.BaseServiceException;
import com.shimi.gsf.core.model.Entity;
import com.shimi.gsf.core.model.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class IssueServiceImpl extends BaseServiceImpl<Issue, IssueFilter> implements IssueService {
    public static final Logger log = LoggerFactory.getLogger(IssueServiceImpl.class);
    @Autowired
    private IssueRepository repository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ComponentService componentService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private IssueGroupService groupService;
    @Autowired
    private FileService fileService;

    @Override
    protected IssueRepository getRepository() {
        return repository;
    }

    @Override
    public Issue get(Long id) {
        Issue issue = super.get(id);
        ProjectMemberUtils.checkMember(projectService.get(issue.getProject().getId()), getCurrentUser());
        return issue;
    }

    @Override
    protected void beforeCreate(Issue issue) {
        Project project = projectService.get(issue.getProject().getId());
        ProjectMemberUtils.checkDeveloper(project, getCurrentUser());

        issue.setProject(project);
        issue.setCode(this.generateIssueCode(project.getId()));

        User currentUser = getCurrentUser();

        if (issue.getSprint() == null) {
            issue.setSprint(project.getBacklogSprint());
        }

        if (issue.getIssueGroup() == null) {
            issue.setIssueGroup(project.getToDoIssueGroup());
        }

        if (issue.getIssueGroup().getStatus().equals(IssueGroupStatus.DONE)) {
            issue.setCompletedTime(new Date());
        }

        if (!CollectionUtils.isEmpty(issue.getFiles())) {
            issue.getFiles().forEach(file -> file.setAllTraceInfo(currentUser));
        }

        // Load the component from DB in case the front-end only passed in the ID of the component
        if (issue.getComponent() != null && issue.getComponent().getId() != null && issue.getComponent().getName() == null) {
            issue.setComponent(componentService.get(issue.getComponent().getId()));
        }
    }

    private synchronized String generateIssueCode(Long projectId) {
        return projectService.generateNextIssueCode(projectId);
    }

    @Override
    protected void afterCreate(Issue issue) {
        sprintService.refreshSprintIssueCount(issue.getSprint().getId());
    }

    @Override
    protected void beforeUpdate(Long id, Issue existingIssue, Issue newIssue) {
        ProjectMemberUtils.checkDeveloper(projectService.get(existingIssue.getProject().getId()), getCurrentUser());

        if (newIssue.getSprint() == null) {
            newIssue.setSprint(existingIssue.getProject().getBacklogSprint());
        }

        if (newIssue.getIssueGroup().getStatus().equals(IssueGroupStatus.DONE)) {
            if (!existingIssue.getIssueGroup().getStatus().equals(newIssue.getIssueGroup().getStatus())) {
                newIssue.setCompletedTime(new Date());
            }
        } else {
            newIssue.setCompletedTime(null);
        }

        User currentUser = getCurrentUser();

        if (!CollectionUtils.isEmpty(newIssue.getFiles())) {
            newIssue.getFiles().forEach(file -> {
                if (file.getCreatedBy() == null) {
                    file.setAllTraceInfo(currentUser);
                }
            });
        }

        // Reload the owner from DB in case the front-end only passed in the ID of the owner
        if (newIssue.getOwner() != null && newIssue.getOwner().getId() != null) {
            newIssue.setOwner(userService.get(newIssue.getOwner().getId()));
        }
    }

    @Override
    protected void afterUpdate(Long id, Issue oldIssue, Issue newIssue) {
        if (!Objects.equals(oldIssue.getSprint().getId(), newIssue.getSprint().getId())) {
            sprintService.refreshSprintIssueCount(oldIssue.getSprint().getId());
            sprintService.refreshSprintIssueCount(newIssue.getSprint().getId());
        }
    }

    protected String[] getUpdateIgnoredProps() {
        return new String[]{"id", "code", "project", "createdTime", "createdBy", "linkToIssues", "linkedByIssues", "comments", "files"};
    }

    @Override
    public void updateIssuesSeq(List<Long> issueIds) {
        List<Issue> newIssues = IntStream.range(0, issueIds.size())
                .mapToObj(i -> {
                    Issue issue = this.get(issueIds.get(i));
                    issue.setSeq(i);
                    return issue;
                }).collect(Collectors.toList());

        User currentUser = getCurrentUser();
        ProjectMemberUtils.checkDeveloper(projectService.get(newIssues.getFirst().getProject().getId()), currentUser);

        repository.saveAll(newIssues);
        log.info("Issue seq updated with IDs in : {}", issueIds);

        if(!CollectionUtils.isEmpty(newIssues)) {
            Issue issue = newIssues.getFirst();
            this.eventPublisher.publishEvent(new IssueSeqUpdatedEvent(issue.getIssueGroup(), issue.getSprint().getId(), currentUser));
        }
    }

    @Override
    public Issue moveIssueToGroup(Long issueId, Long groupId) {
        Issue issue = this.get(issueId);
        Issue newIssue = new Issue();
        BeanUtils.copyProperties(issue, newIssue);

        if (issue.getIssueGroup() == null || !issue.getIssueGroup().getId().equals(groupId)) {
            IssueGroup issueGroup = groupService.get(groupId);
            newIssue.setIssueGroup(issueGroup);
            return super.update(issueId, newIssue);
        } else {
            return issue;
        }
    }

    private Issue moveIssueToSprint(Long issueId, Long sprintId) {
        Issue issue = this.get(issueId);
        Issue newIssue = new Issue();
        BeanUtils.copyProperties(issue, newIssue);

        if (issue.getSprint() == null || !issue.getSprint().getId().equals(sprintId)) {
            Sprint sprint = sprintService.get(sprintId);
            newIssue.setSprint(sprint);
            return super.update(issueId, newIssue);
        } else {
            return issue;
        }
    }

    @Override
    public File addFile(Long issueId, File file) {
        Issue issue = this.get(issueId);
        Project project = projectService.get(issue.getProject().getId());
        Long projectId = project.getId();
        ProjectMemberUtils.checkDeveloper(project, getCurrentUser());
        file.setProjectId(projectId);
        file.setIssue(issue);
        File savedFile = fileService.create(file);
        log.info("Issue {} added attachment {}", issueId, savedFile.getId());
        return savedFile;
    }

    @Override
    public void deleteFile(Long issueId, Long fileId) {
        Issue issue = this.get(issueId);
        ProjectMemberUtils.checkDeveloper(projectService.get(issue.getProject().getId()), getCurrentUser());
        File file = fileService.get(fileId);
        if (!Objects.equals(file.getIssue().getId(), issueId)) {
            throw new BaseServiceException("File with ID " + fileId + " does not belong to issue with ID " + issueId);
        }
        fileService.delete(fileId);
        log.info("File {} deleted from issue {}", fileId, issueId);
    }

    @Override
    public Issue assignTo(Long issueId, Long userId) {
        Issue issue = this.get(issueId);

        if (issue.getOwner() == null || !issue.getOwner().getId().equals(userId)) {
            User user = userService.get(userId);
            issue.setOwner(user);
            return this.update(issueId, issue);
        } else {
            return issue;
        }
    }

    @Override
    public Issue unassign(Long issueId) {
        Issue issue = this.get(issueId);

        if (issue.getOwner() != null) {
            issue.setOwner(null);
            return this.update(issueId, issue);
        } else {
            return issue;
        }
    }

    @Override
    public void linkIssue(Long issueId, Long linkToIssueId) {
        Issue issue = this.get(issueId);
        issue.getLinkToIssues().add(this.get(linkToIssueId));
        repository.save(issue);
        log.info("Issue with ID {} linked with issue with ID {}", issueId, linkToIssueId);
    }

    @Override
    public void unlinkIssue(Long issueId, Long linkToIssueId) {
        Issue issue = this.get(issueId);
        List<Issue> currentLinkTos = issue.getLinkToIssues();
        List<Issue> updatedLinkTos = currentLinkTos.stream()
                .filter(linkIssue -> !linkIssue.getId().equals(linkToIssueId)).collect(Collectors.toList());
        issue.setLinkToIssues(updatedLinkTos);

        List<Issue> currentLinkedBys = issue.getLinkedByIssues();
        List<Issue> updatedLinkedBys = currentLinkedBys.stream()
                .filter(linkIssue -> !linkIssue.getId().equals(linkToIssueId)).collect(Collectors.toList());
        issue.setLinkedByIssues(updatedLinkedBys);

        repository.save(issue);
        log.info("Issue with ID {} unlinked with issue with ID {}", issueId, linkToIssueId);
    }

    @Override
    protected Specification<Issue> toSpec(IssueFilter filter) {
        Specification<Issue> querySpec = null;

        if (filter.getProjectId() != null) {
            querySpec = IssueSpecs.projectIdEquals(filter.getProjectId());
        }

        if (!CollectionUtils.isEmpty(filter.getTypes())) {
            Specification<Issue> typeIn = IssueSpecs.typeIn(filter.getTypes());

            querySpec = Objects.isNull(querySpec) ? typeIn : querySpec.and(typeIn);
        }

        if (!CollectionUtils.isEmpty(filter.getPriorities())) {
            Specification<Issue> priorityIn = IssueSpecs.priorityIn(filter.getPriorities());

            querySpec = Objects.isNull(querySpec) ? priorityIn : querySpec.and(priorityIn);
        }

        if (Boolean.TRUE.equals(filter.getBacklog())) {
            Specification<Issue> inBacklog = IssueSpecs.inBacklog();
            querySpec = Objects.isNull(querySpec) ? inBacklog : querySpec.and(inBacklog);
        }

        if (!CollectionUtils.isEmpty(filter.getSprintIds())) {
            Specification<Issue> sprintIdIn = IssueSpecs.sprintIdIn(filter.getSprintIds());

            querySpec = Objects.isNull(querySpec) ? sprintIdIn : querySpec.and(sprintIdIn);
        }

        if (!CollectionUtils.isEmpty(filter.getGroupIds())) {
            Specification<Issue> groupIdIn = IssueSpecs.groupIdIn(filter.getGroupIds());

            querySpec = Objects.isNull(querySpec) ? groupIdIn : querySpec.and(groupIdIn);
        }

        if (!CollectionUtils.isEmpty(filter.getComponentIds())) {
            Specification<Issue> componentIdIn = IssueSpecs.componentIdIn(filter.getComponentIds());

            querySpec = Objects.isNull(querySpec) ? componentIdIn : querySpec.and(componentIdIn);
        }

        if (!CollectionUtils.isEmpty(filter.getOwnerIds())) {
            Specification<Issue> ownerIdIn = IssueSpecs.ownerIdIn(filter.getOwnerIds());

            querySpec = Objects.isNull(querySpec) ? ownerIdIn : querySpec.and(ownerIdIn);
        }

        if (!CollectionUtils.isEmpty(filter.getTagIds())) {
            Specification<Issue> tagIdIn = IssueSpecs.tagIdIn(filter.getTagIds());

            querySpec = Objects.isNull(querySpec) ? tagIdIn : querySpec.and(tagIdIn);
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            String keyword = filter.getKeyword();
            Specification<Issue> codeOrNameLike = IssueSpecs.codeLike(keyword).or(IssueSpecs.nameLike(keyword));

            querySpec = Objects.isNull(querySpec) ? codeOrNameLike : querySpec.and(codeOrNameLike);
        }

        if (filter.getTestCaseId() != null) {
            Specification<Issue> testCaseIdEquals = IssueSpecs.testCaseIdEquals(filter.getTestCaseId());
            querySpec = Objects.isNull(querySpec) ? testCaseIdEquals : querySpec.and(testCaseIdEquals);
        }

        if (filter.getTestPlanId() != null) {
            Specification<Issue> testPlanIdEquals = IssueSpecs.testPlanIdEquals(filter.getTestPlanId());
            querySpec = Objects.isNull(querySpec) ? testPlanIdEquals : querySpec.and(testPlanIdEquals);
        }

        return querySpec;
    }

    @Override
    public Issue update(Long id, Issue issue) {
        Issue exitingIssue = get(id);

        if (exitingIssue.getIssueGroup() != null && exitingIssue.getSprint() != null &&
                (!exitingIssue.getIssueGroup().getId().equals(issue.getIssueGroup().getId())
                || !exitingIssue.getSprint().getId().equals(issue.getSprint().getId()))) {
            Integer seq = repository.getLastSeq(issue.getIssueGroup().getId(), issue.getSprint().getId());
            if (seq == null) {
                issue.setSeq(0);
            } else {
                issue.setSeq(seq + 1);
            }
        }

        // Reload the component from DB in case the front-end only passed in the ID of the component
        if (issue.getComponent() != null && issue.getComponent().getId() != null && issue.getComponent().getName() == null) {
            issue.setComponent(componentService.get(issue.getComponent().getId()));
        }

        return super.update(id, issue);
    }

    public Issue cloneIssue(Long issueId) {
        Issue sourceIssue = this.get(issueId);

        Issue clonedIssue = new Issue();
        BeanUtils.copyProperties(sourceIssue, clonedIssue, "id", "comments", "files", "linkToIssues", "linkedByIssues",
                "tags", "owner");
        clonedIssue.setTags(new ArrayList<>(sourceIssue.getTags()));
        clonedIssue.setName("Copy of " + sourceIssue.getName());

        return this.create(clonedIssue);
    }

    @Override
    protected void beforeDelete(Issue issue) {
        ProjectMemberUtils.checkDeveloper(projectService.get(issue.getProject().getId()), getCurrentUser());
    }

    @Override
    protected void afterDelete(Issue issue) {
        List<File> files = issue.getFiles();
        // Need to manually delete files from storage, which cannot be done by JPA cascading
        if(!CollectionUtils.isEmpty(files)) {
            files.forEach(file -> {
                this.fileService.delete(file.getId());
            });

            log.info("Deleted {} files of issue {}", files.size(), issue);
        }

        sprintService.refreshSprintIssueCount(issue.getSprint().getId());
    }

    @Override
    public void moveIssuesToSprint(List<Long> issueIds, Long targetSprintId) {
        issueIds.forEach(id -> this.moveIssueToSprint(id, targetSprintId));
    }

    @Override
    public List<IssueCountDto> countIssueByStatus(Long sprintId) {
        Sprint sprint = sprintService.get(sprintId);
        List<IssueGroup> issueGroups = sprint.getProject().getIssueGroups();
        List<IssueCountDto> issueCountDtos = this.repository.countIssueByStatus(sprintId);

        // Fill in zero for those empty groups
        issueGroups.forEach(issueGroup -> {
            boolean noneMatch = issueCountDtos.stream().noneMatch(dto -> {
                if (dto.getStoryPoints() == null) {
                    dto.setStoryPoints(0D);
                }
                return dto.getIssueGroupId().equals(issueGroup.getId());
            });

            if (noneMatch) {
                issueCountDtos.add(new IssueCountDto(issueGroup.getId(), 0L, 0D));
            }
        });

        return issueCountDtos;
    }

    @Override
    public List<Issue> findBySprintId(Long sprintId) {
        return repository.findBySprintId(sprintId);
    }

    @Override
    public byte[] export(IssueFilter filter) {
        filter.getOrders().add(new Filter.Order("id", Filter.Direction.ASC));
        List<Issue> issues = this.searchAll(filter);
        List<String> enHeaders = Arrays.asList("Key", "Title", "Type", "Priority", "Status", "Story point", "Component", "Estimated hours", "Actual hours",
                "Sprint", "Reporter", "Created time", "Updated time", "Due date", "Assignee", "Test case", "Test plan", "Tags", "Description", "Comments");
        List<String> cnHeaders = Arrays.asList("代码", "标题", "类型", "优先级", "状态", "故事点", "功能模块", "预估工时", "实际工时",
                "迭代", "提交者", "创建时间", "更新时间", "截止时间", "执行者", "测试用例", "测试计划", "标签", "描述", "评论");
        byte[] result = Exporter.exportExcel("Issues", "cn".equalsIgnoreCase(filter.getLanguage()) ? cnHeaders : enHeaders, toExcelBodyRows(issues));
        log.info("Exported {} issues to Excel for filter: {}", issues.size(), filter);
        return result;
    }

    private List<List<Object>> toExcelBodyRows(List<Issue> issues) {
        return issues.stream().map(this::toExcelBodyRow).toList();
    }

    private List<Object> toExcelBodyRow(Issue issue) {
        List<Object> cells = new ArrayList<>();

        cells.add(issue.getProject().getCode() + '-' + issue.getCode());
        cells.add(issue.getName());
        cells.add(issue.getType().name());
        cells.add(issue.getPriority().name());
        cells.add(issue.getIssueGroup() != null ? issue.getIssueGroup().getLabel() : null);
        cells.add(issue.getStoryPoints() != null ? issue.getStoryPoints() : null);
        cells.add(issue.getComponent() != null ? issue.getComponent().getName() : null);
        cells.add(issue.getEstimatedHours() != null ? issue.getEstimatedHours() : null);
        cells.add(issue.getActualHours() != null ? issue.getActualHours() : null);
        cells.add(issue.getSprint() != null ? issue.getSprint().getName() : null);
        cells.add(issue.getCreatedBy().getNickname());
        cells.add(issue.getCreatedTime());
        cells.add(issue.getUpdatedTime());
        cells.add(issue.getDueTime());
        cells.add(issue.getOwner() != null ? issue.getOwner().getNickname() : null);
        cells.add(issue.getTestCase() != null ? issue.getTestCase().getDetails().getName() : null);
        cells.add(issue.getTestPlan() != null ? issue.getTestPlan().getName() : null);
        cells.add(!CollectionUtils.isEmpty(issue.getTags()) ? getTagNames(issue) : null);
        cells.add(issue.getDescription());
        cells.add(!CollectionUtils.isEmpty(issue.getComments()) ? getComments(issue) : null);

        return cells;
    }

    private String getTagNames(Issue issue) {
        List<String> tagNames = issue.getTags().stream().map(Tag::getName).toList();
        return String.join(", ", tagNames);
    }

    private String getComments(Issue issue) {
        List<String> commentRows = issue.getComments().stream().map(Comment::format).toList();
        return String.join(";\n", commentRows);
    }

    /**
     * Move issues to parent component after deleting a component.
     * @param event the entity change event
     */
    @EventListener
    public void moveIssuesToParentComponent(EntityChangeEvent event) {
        Entity entity = Objects.requireNonNullElse(event.getPreviousEntity(), event.getUpdatedEntity());

        if (!(entity instanceof Component) || !EntityChangeEvent.ActionType.DELETE.equals(event.getActionType())) {
            return;
        }

        Component component = (Component)event.getPreviousEntity();

        IssueFilter filter = new IssueFilter();
        filter.setPageSize(Integer.MAX_VALUE);
        filter.setComponentIds(Collections.singletonList(component.getId()));
        List<Issue> issues = this.search(filter).getResults();

        if (!CollectionUtils.isEmpty(issues)) {
            issues.forEach(issue -> issue.setComponent(new Component(component.getParentId())));
            repository.saveAll(issues);
            log.info("Updated {} issues' componentId to {} after deletion of component {}", issues.size(), component.getParentId(), component);
        }
    }
}