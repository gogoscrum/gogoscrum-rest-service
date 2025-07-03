package com.shimi.gogoscrum.issue.repository;

import com.shimi.gogoscrum.issue.dto.IssueCountDto;
import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IssueRepository extends GeneralRepository<Issue> {
    @Query("select max(seq) from Issue i where (i.issueGroup.id = :groupId and i.sprint.id = :sprintId)")
    Integer getLastSeq(Long groupId, Long sprintId);

    List<Issue> findBySprintId(Long sprintId);

    @Query(value = "select new com.shimi.gogoscrum.issue.dto.IssueCountDto(i.issueGroup.id, count(i.id), sum(i.storyPoints)) " +
            " from Issue i" +
            " where i.sprint.id = :sprintId group by i.issueGroup")
    List<IssueCountDto> countIssueByStatus(Long sprintId);
}
