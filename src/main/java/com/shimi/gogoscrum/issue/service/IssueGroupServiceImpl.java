package com.shimi.gogoscrum.issue.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.issue.event.GroupSeqUpdatedEvent;
import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gogoscrum.issue.model.IssueFilter;
import com.shimi.gogoscrum.issue.model.IssueGroup;
import com.shimi.gogoscrum.issue.model.IssueGroupFilter;
import com.shimi.gogoscrum.issue.repository.IssueGroupRepository;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.exception.BaseServiceException;
import com.shimi.gsf.core.exception.EntityDuplicatedException;
import com.shimi.gsf.core.model.EntityQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class IssueGroupServiceImpl extends BaseServiceImpl<IssueGroup, IssueGroupFilter> implements IssueGroupService {
    public static final Logger log = LoggerFactory.getLogger(IssueGroupServiceImpl.class);
    @Autowired
    private IssueGroupRepository repository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private IssueService issueService;

    @Override
    protected IssueGroupRepository getRepository() {
        return repository;
    }

    @Override
    public IssueGroup get(Long id) {
        IssueGroup issueGroup = super.get(id);
        ProjectMemberUtils.checkMember(projectService.get(issueGroup.getProject().getId()), getCurrentUser());
        return issueGroup;
    }

    @Override
    public void updateSeq(List<Long> issueGroupIds) {
        List<IssueGroup> newIssueGroups = IntStream.range(0, issueGroupIds.size())
                .mapToObj(i -> {
                    IssueGroup issueGroup = this.get(issueGroupIds.get(i));
                    issueGroup.setSeq((short) i);
                    return issueGroup;
                }).toList();

        User currentUser = getCurrentUser();
        ProjectMemberUtils.checkDeveloper(projectService.get(newIssueGroups.getFirst().getProject().getId()),
                currentUser);
        repository.saveAll(newIssueGroups);

        log.info("IssueGroup seq updated in IDs： {}", issueGroupIds);

        CollectionUtils.isEmpty(newIssueGroups);
        IssueGroup group = newIssueGroups.getFirst();
        this.eventPublisher.publishEvent(new GroupSeqUpdatedEvent(group.getProject(), group.getProject().getId(), currentUser));
    }

    @Override
    protected String[] getUpdateIgnoredProps() {
        return new String[]{"id", "createdTime", "createdBy", "project", "builtIn", "status"};
    }

    @Override
    protected void beforeCreate(IssueGroup issueGroup) {
        ProjectMemberUtils.checkDeveloper(projectService.get(issueGroup.getProject().getId()), getCurrentUser());
        checkDuplicatedGroupLabel(issueGroup, issueGroup.getProject().getId());
    }

//    @Override
//    protected void afterCreate(IssueGroup issueGroup) {
//        this.eventPublisher.publishEvent(new IssueGroupEvent(MultipleActionEvent.ActionType.CREATE, issueGroup, issueGroup));
//    }

    @Override
    protected void beforeUpdate(Long id, IssueGroup existingEntity, IssueGroup newEntity) {
        ProjectMemberUtils.checkDeveloper(projectService.get(existingEntity.getProject().getId()), getCurrentUser());
        checkDuplicatedGroupLabel(newEntity, existingEntity.getProject().getId());
    }

//    @Override
//    protected void afterUpdate(Long id, IssueGroup oldGroup, IssueGroup newGroup) {
//        this.eventPublisher.publishEvent(new IssueGroupEvent(MultipleActionEvent.ActionType.UPDATE, oldGroup, newGroup));
//    }

    private void checkDuplicatedGroupLabel(IssueGroup issueGroup, Long projectId) {
        IssueGroup existingGroup = repository.getByProjectIdAndLabelEquals(projectId, issueGroup.getLabel());

        if (existingGroup != null && !existingGroup.getId().equals(issueGroup.getId())) {
            throw new EntityDuplicatedException(ErrorCode.DUPLICATED_GROUP, "Duplicated group label already exists");
        }
    }

    @Override
    protected void beforeDelete(IssueGroup issueGroup) {
        ProjectMemberUtils.checkDeveloper(projectService.get(issueGroup.getProject().getId()), getCurrentUser());

        if (issueGroup.isBuiltIn()) {
            throw new BaseServiceException(ErrorCode.CANNOT_BE_DELETED, "This issue group is built-in and cannot be deleted", HttpStatus.NOT_ACCEPTABLE);
        }

        this.moveIssuesIntoTodo(issueGroup);
    }

//    @Override
//    protected void afterDelete(IssueGroup issueGroup) {
//        this.eventPublisher.publishEvent(new IssueGroupEvent(MultipleActionEvent.ActionType.DELETE, issueGroup, issueGroup));
//    }

    /**
     * Move all the issues in the deleting group into the sprint backlog (e.g. the to do list of each sprint)
     * @param deletingGroup the issue group to be deleted
     */
    private void moveIssuesIntoTodo(IssueGroup deletingGroup) {
        IssueFilter filter = new IssueFilter();
        filter.setPageSize(Integer.MAX_VALUE);
        filter.setGroupIds(Collections.singletonList(deletingGroup.getId()));

        EntityQueryResult<Issue> queryResult = issueService.search(filter);
        List<Issue> issues = queryResult.getResults();
        IssueGroup toDoIssueGroup = deletingGroup.getProject().getToDoIssueGroup();

        if (toDoIssueGroup != null && !CollectionUtils.isEmpty(issues)) {
            issues.forEach(issue -> {
                issue.setIssueGroup(toDoIssueGroup);
                // Put those issues at the bottom of the to do list
                issue.setSeq(999);
            });
            issueService.saveAll(issues);

            log.info("{} issues moved from group {} (id = {}) into todo status (id = {})", issues.size(),
                    deletingGroup.getLabel(), deletingGroup.getId(), toDoIssueGroup.getId());
        }
    }

    @Override
    protected Specification<IssueGroup> toSpec(IssueGroupFilter filter) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
