package com.shimi.gogoscrum.issue.repository;

import com.shimi.gogoscrum.issue.model.IssueFilter;
import com.shimi.gsf.core.repository.GeneralRepository;

import java.util.List;

public interface IssueFilterRepository extends GeneralRepository<IssueFilter> {
    List<IssueFilter> findByProjectIdAndCreatedByIdOrderBySeq(Long projectId, Long userId);
}
