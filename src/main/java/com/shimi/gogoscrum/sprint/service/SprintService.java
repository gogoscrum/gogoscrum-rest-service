package com.shimi.gogoscrum.sprint.service;

import com.shimi.gogoscrum.issue.dto.CumulativeFlowDiagramDto;
import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.sprint.model.Sprint;
import com.shimi.gogoscrum.sprint.model.SprintFilter;
import com.shimi.gogoscrum.sprint.model.SprintIssueCount;
import com.shimi.gsf.core.service.GeneralService;

import java.util.List;

public interface SprintService extends GeneralService<Sprint, SprintFilter> {
    Sprint createBacklog(Project project);
    List<Sprint> findAllActiveSprints();
    SprintIssueCount getSprintIssueCount(Long sprintId);
    SprintIssueCount saveSprintIssueCount(SprintIssueCount sprintIssueCount);
    CumulativeFlowDiagramDto getSprintCumulativeFlowDiagram(Long sprintId);
}
