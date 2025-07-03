package com.shimi.gogoscrum.issue.service;

import com.shimi.gogoscrum.issue.model.IssueGroup;
import com.shimi.gogoscrum.issue.model.IssueGroupFilter;
import com.shimi.gsf.core.service.GeneralService;

import java.util.List;

public interface IssueGroupService extends GeneralService<IssueGroup, IssueGroupFilter> {
    void updateSeq(List<Long> issueGroupIds);
}
