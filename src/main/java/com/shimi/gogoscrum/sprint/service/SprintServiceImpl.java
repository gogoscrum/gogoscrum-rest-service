package com.shimi.gogoscrum.sprint.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.common.util.DateTimeUtil;
import com.shimi.gogoscrum.issue.dto.CumulativeFlowDiagramDto;
import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gogoscrum.issue.repository.IssueRepository;
import com.shimi.gogoscrum.issue.service.IssueService;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.sprint.model.Sprint;
import com.shimi.gogoscrum.sprint.model.SprintFilter;
import com.shimi.gogoscrum.sprint.model.SprintIssueCount;
import com.shimi.gogoscrum.sprint.repository.SprintIssueCountRepository;
import com.shimi.gogoscrum.sprint.repository.SprintRepository;
import com.shimi.gsf.core.exception.BaseServiceException;
import com.shimi.gsf.core.exception.NoPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SprintServiceImpl extends BaseServiceImpl<Sprint, SprintFilter> implements SprintService {
    private static final Logger log = LoggerFactory.getLogger(SprintServiceImpl.class);
    @Autowired
    private SprintRepository repository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private SprintIssueCountRepository sprintIssueCountRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    @Lazy
    private IssueService issueService;

    @Override
    protected SprintRepository getRepository() {
        return repository;
    }

    @Override
    public Sprint get(Long id) {
        Sprint sprint = super.get(id);
        ProjectMemberUtils.checkMember(sprint.getProject(), getCurrentUser());
        return sprint;
    }

    @Override
    protected void beforeCreate(Sprint sprint) {
        ProjectMemberUtils.checkDeveloper(projectService.get(sprint.getProject().getId()), getCurrentUser());
        this.validate(sprint);
    }

    private void validate(Sprint sprint) {
        if (StringUtils.hasText(sprint.getName())) {
            if (sprint.getStartDate() != null && sprint.getEndDate() != null) {
                sprint.setStartDate(DateTimeUtil.getBeginningOfDay(sprint.getStartDate()));
                sprint.setEndDate(DateTimeUtil.getEndingOfDay(sprint.getEndDate()));
            }
        } else {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "The sprint name cannot be empty", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    protected void beforeDelete(Sprint sprint) {
        ProjectMemberUtils.checkDeveloper(projectService.get(sprint.getProject().getId()), getCurrentUser());

        Long currentUserId = getCurrentUser().getId();
        Long projectOwnerId = sprint.getProject().getOwner().getId();
        Long sprintCreatorId = sprint.getCreatedBy().getId();

        if (!projectOwnerId.equals(currentUserId) && !sprintCreatorId.equals(currentUserId)) {
            throw new NoPermissionException("Only project owner or sprint creator can delete a sprint.");
        } else if (Boolean.TRUE.equals(sprint.getBacklog())) {
            throw new BaseServiceException("backlogCannotBeDeleted", "Project backlog cannot be deleted.", HttpStatus.PRECONDITION_FAILED);
        } else {
            Project project = sprint.getProject();
            Sprint backlogSprint = project.getBacklogSprint();

            if (backlogSprint == null) {
                backlogSprint = this.createBacklog(project);
            }

            List<Issue> issues = sprint.getIssues();

            for (Issue issue : issues) {
                issue.setSprint(backlogSprint);
            }

            issueRepository.saveAll(issues);
            log.debug("{} issues moved into project Backlog: {}", issues.size(), backlogSprint);
        }
    }

    @Override
    public Sprint createBacklog(Project project) {
        Sprint backLog = new Sprint();

        backLog.setName("Backlog");
        backLog.setBacklog(true);
        backLog.setProject(project);

        Sprint savedBacklog = this.create(backLog);
        project.getSprints().add(savedBacklog);
        return savedBacklog;
    }

    @Override
    protected void beforeUpdate(Long id, Sprint existingEntity, Sprint newEntity) {
        ProjectMemberUtils.checkDeveloper(projectService.get(existingEntity.getProject().getId()), getCurrentUser());
        this.validate(newEntity);
    }

    @Override
    public List<Sprint> findAllActiveSprints() {
        Date endingOfYesterday = DateTimeUtil.getEndingOfYesterday();
        return this.repository.findAllActiveSprints(endingOfYesterday);
    }

    @Override
    public SprintIssueCount getSprintIssueCount(Long sprintId) {
        return this.sprintIssueCountRepository.findBySprintId(sprintId);
    }

    @Override
    public CumulativeFlowDiagramDto getSprintCumulativeFlowDiagram(Long sprintId) {
        SprintIssueCount sprintIssueCount = this.getSprintIssueCount(sprintId);

        Sprint sprint = this.get(sprintId);

        CumulativeFlowDiagramDto cumulativeFlowDiagramDto = this.initCumulativeFlowDiagramDto(sprint);

        if (sprintIssueCount != null) {
            // Append today's counts if it doesn't contain
            if (sprint.isActive()) {
                String todayDateLabel = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                sprintIssueCount.getDailyCounts().put(todayDateLabel, issueService.countIssueByStatus(sprintId));
            }

            this.fillInDailyCounts(sprintIssueCount, cumulativeFlowDiagramDto);
        }

        return cumulativeFlowDiagramDto;
    }

    private CumulativeFlowDiagramDto initCumulativeFlowDiagramDto (Sprint sprint) {
        CumulativeFlowDiagramDto cumulativeFlowDiagramDto = new CumulativeFlowDiagramDto(sprint.getId());
        Project project = sprint.getProject();

        Map<Long, CumulativeFlowDiagramDto.StatusGroupLineDto> groupLines =
                cumulativeFlowDiagramDto.getGroupLines();

        project.getIssueGroups().forEach(group -> groupLines.put(group.getId(), new CumulativeFlowDiagramDto.StatusGroupLineDto(group.getId(), group.getLabel())));

        return cumulativeFlowDiagramDto;
    }

    private void fillInDailyCounts(SprintIssueCount sprintIssueCount, CumulativeFlowDiagramDto cumulativeFlowDiagramDto) {
        sprintIssueCount.getDailyCounts().forEach((key, countsByGroup) -> {
            Map<Long, CumulativeFlowDiagramDto.StatusGroupLineDto> groupLines =
                    cumulativeFlowDiagramDto.getGroupLines();

            if (!CollectionUtils.isEmpty(countsByGroup)) {
                countsByGroup.forEach(groupCount -> {
                    Long groupId = groupCount.getIssueGroupId();
                    CumulativeFlowDiagramDto.StatusGroupLineDto groupLineDto = groupLines.get(groupId);
                    Map<String, CumulativeFlowDiagramDto.DailyCountDto> dailyCounts = groupLineDto.getDailyCounts();
                    dailyCounts.put(key, new CumulativeFlowDiagramDto.DailyCountDto(groupCount.getCount(), groupCount.getStoryPoints()));
                });
            }
        });
    }

    @Override
    public SprintIssueCount saveSprintIssueCount(SprintIssueCount sprintIssueCount) {
        return this.sprintIssueCountRepository.save(sprintIssueCount);
    }

    public void refreshSprintIssueCount(Long sprintId) {
        Sprint sprint = repository.getReferenceById(sprintId);
        long oldTotalIssueCount = sprint.getTotalIssueCount();
        long oldDoneIssueCount = sprint.getDoneIssueCount();

        List<Issue> issues = issueService.findBySprintId(sprintId);
        long totalIssueCount = issues.size();
        long doneIssueCount = issues.stream().filter(Issue::isDone).count();

        if (totalIssueCount != oldTotalIssueCount || doneIssueCount != oldDoneIssueCount) {
            sprint.setTotalIssueCount(totalIssueCount);
            sprint.setDoneIssueCount(doneIssueCount);
            repository.save(sprint);

            if (log.isDebugEnabled()) {
                log.debug("Updated issue count of sprint {}: total {} -> {}, done {} -> {}",
                        sprintId, oldTotalIssueCount, totalIssueCount, oldDoneIssueCount, doneIssueCount);
            }
        }
    }

    @Override
    protected Specification<Sprint> toSpec(SprintFilter filter) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
