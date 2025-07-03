package com.shimi.gogoscrum.issue.service;

import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.issue.dto.IssueCountDto;
import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gogoscrum.issue.model.IssueFilter;
import com.shimi.gsf.core.service.GeneralService;

import java.util.List;

public interface IssueService extends GeneralService<Issue, IssueFilter> {
    Issue copyIssue(Long issueId);

    void updateIssuesSeq(List<Long> issueIds);

    Issue moveIssueToGroup(Long issueId, Long groupId);

    void moveIssuesToSprint(List<Long> issueIds, Long targetSprintId);

    Issue assignTo(Long issueId, Long userId);

    Issue unassign(Long issueId);

    void linkIssue(Long issueId, Long linkToIssueId);

    void unlinkIssue(Long issueId, Long linkToIssueId);

    List<IssueCountDto> countIssueByStatus(Long sprintId);

    List<Issue> findBySprintId(Long sprintId);

    byte[] export(IssueFilter filter);

    File addFile(Long issueId, File file);

    void deleteFile(Long issueId, Long fileId);
}
