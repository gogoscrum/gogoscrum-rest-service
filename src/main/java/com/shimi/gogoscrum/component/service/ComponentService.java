package com.shimi.gogoscrum.component.service;

import com.shimi.gogoscrum.component.model.Component;
import com.shimi.gogoscrum.component.model.ComponentFilter;
import com.shimi.gsf.core.service.GeneralService;

import java.util.List;

public interface ComponentService extends GeneralService<Component, ComponentFilter> {
    void updateSeq(List<Long> componentIds);
    List<Component> findByProjectId(Long projectId);
}
