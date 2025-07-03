package com.shimi.gogoscrum.issue.repository;

import com.shimi.gogoscrum.issue.model.IssueGroup;
import com.shimi.gsf.core.repository.GeneralRepository;

public interface IssueGroupRepository extends GeneralRepository<IssueGroup> {
    IssueGroup getByProjectIdAndLabelEquals(Long id, String label);
}
