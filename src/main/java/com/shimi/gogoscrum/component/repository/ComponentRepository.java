package com.shimi.gogoscrum.component.repository;

import com.shimi.gogoscrum.component.model.Component;
import com.shimi.gsf.core.repository.GeneralRepository;

import java.util.List;

public interface ComponentRepository extends GeneralRepository<Component> {
    Long countByParentId(Long id);
    List<Component> findByProjectId(Long projectId);
}
