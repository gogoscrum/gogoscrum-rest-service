package com.shimi.gogoscrum.component.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.component.model.Component;
import com.shimi.gogoscrum.component.model.ComponentFilter;
import com.shimi.gogoscrum.component.repository.ComponentRepository;
import com.shimi.gogoscrum.issue.service.IssueService;
import com.shimi.gsf.core.exception.BaseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ComponentServiceImpl extends BaseServiceImpl<Component, ComponentFilter> implements ComponentService {
    private static final Logger log = LoggerFactory.getLogger(ComponentServiceImpl.class);
    @Autowired
    private ComponentRepository repository;
    @Autowired
    private IssueService issueService;

    @Override
    protected ComponentRepository getRepository() {
        return repository;
    }

    @Override
    public void updateSeq(List<Long> componentIds) {
        AtomicInteger i = new AtomicInteger();

        List<Component> categories = componentIds.stream().map(id -> {
            Component cat = get(id);
            cat.setSeq(i.getAndIncrement());
            return cat;
        }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(categories)) {
            repository.saveAll(categories);

            log.info("Updated components seq for {}", componentIds);
        }
    }

    @Override
    public List<Component> findByProjectId(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    @Override
    protected void beforeCreate(Component component) {
        Long parentId = component.getParentId();

        if (parentId == null || parentId <= 0L) {
            component.setParentId(null);
            component.setPath("/");
        } else {
            Component parent = get(parentId);
            component.setPath(parent.getFullPath());
        }
    }

    @Override
    protected void beforeDelete(Component component) {
        Long childrenCount = repository.countByParentId(component.getId());

        if (childrenCount > 0) {
            throw new BaseServiceException(ErrorCode.HAS_CHILDREN, "Component has children and cannot be deleted",
                    HttpStatus.PRECONDITION_FAILED);
        }

        issueService.moveIssuesToParentComponent(component.getId());
    }

    @Override
    protected Specification<Component> toSpec(ComponentFilter filter) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
