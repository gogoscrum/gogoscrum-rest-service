package com.shimi.gogoscrum.issue.service;

import com.shimi.gogoscrum.issue.model.IssueFilter;
import com.shimi.gogoscrum.issue.model.IssueFilterFilter;
import com.shimi.gsf.core.service.GeneralService;

import java.util.List;

public interface IssueFilterService extends GeneralService<IssueFilter, IssueFilterFilter> {
    List<IssueFilter> findMyFilters(Long projectId);
    IssueFilter copyFilter(Long id);
    void updateSeq(List<Long> filterIds);
}
