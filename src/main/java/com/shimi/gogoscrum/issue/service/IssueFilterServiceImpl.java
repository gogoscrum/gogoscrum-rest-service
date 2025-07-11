package com.shimi.gogoscrum.issue.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.common.util.PermissionUtil;
import com.shimi.gogoscrum.issue.model.IssueFilter;
import com.shimi.gogoscrum.issue.model.IssueFilterFilter;
import com.shimi.gogoscrum.issue.repository.IssueFilterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class IssueFilterServiceImpl extends BaseServiceImpl<IssueFilter, IssueFilterFilter> implements IssueFilterService {
    public static final Logger log = LoggerFactory.getLogger(IssueFilterServiceImpl.class);
    @Autowired
    private IssueFilterRepository repository;

    @Override
    protected IssueFilterRepository getRepository() {
        return repository;
    }

    @Override
    public List<IssueFilter> findMyFilters(Long projectId) {
        return repository.findByProjectIdAndCreatedByIdOrderBySeq(projectId, getCurrentUser().getId());
    }

    @Override
    protected void beforeDelete(IssueFilter filter) {
        PermissionUtil.checkOwnership(filter, getCurrentUser());
    }

    @Override
    protected Specification<IssueFilter> toSpec(IssueFilterFilter filter) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public IssueFilter copyFilter(Long id) {
        IssueFilter existingFilter = get(id);
        IssueFilter newFilter = new IssueFilter();

        BeanUtils.copyProperties(existingFilter, newFilter);
        newFilter.setName("Copy of " + existingFilter.getName());

        return super.create(newFilter);
    }

    @Override
    public void updateSeq(List<Long> filterIds) {
        List<IssueFilter> newFilters = IntStream.range(0, filterIds.size())
                .mapToObj(i -> {
                    IssueFilter filter = this.get(filterIds.get(i));
                    filter.setSeq(i);
                    return filter;
                }).collect(Collectors.toList());

        repository.saveAll(newFilters);
        log.info("Issue filter seq updated with IDs : {}", filterIds);
    }
}