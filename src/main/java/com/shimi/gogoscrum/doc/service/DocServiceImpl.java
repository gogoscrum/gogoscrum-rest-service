package com.shimi.gogoscrum.doc.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.doc.model.Doc;
import com.shimi.gogoscrum.doc.model.DocFilter;
import com.shimi.gogoscrum.doc.repository.DocRepository;
import com.shimi.gogoscrum.doc.repository.DocSpecs;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class DocServiceImpl extends BaseServiceImpl<Doc, DocFilter> implements DocService {
    @Autowired
    private DocRepository repository;

    @Autowired
    private ProjectService projectService;

    @Override
    protected DocRepository getRepository() {
        return repository;
    }

    @Override
    public Doc get(Long id) {
        Doc doc = super.get(id);
        ProjectMemberUtils.checkMember(projectService.get(doc.getProjectId()), getCurrentUser());
        return doc;
    }

    @Override
    protected Specification<Doc> toSpec(DocFilter filter) {
        Specification<Doc> querySpec = null;

        if (filter.getProjectId() != null) {
            querySpec = DocSpecs.projectIdEqual(filter.getProjectId());
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            Specification<Doc> nameLike = DocSpecs.nameLike(filter.getKeyword());
            querySpec = Objects.isNull(querySpec) ? nameLike : querySpec.and(nameLike);
        }

        return querySpec;
    }

    @Override
    protected void beforeCreate(Doc doc) {
        ProjectMemberUtils.checkDeveloper(projectService.get(doc.getProjectId()), getCurrentUser());
    }

    @Override
    protected void beforeUpdate(Long id, Doc existingEntity, Doc newEntity) {
        ProjectMemberUtils.checkDeveloper(projectService.get(existingEntity.getProjectId()), getCurrentUser());
    }

    @Override
    protected void beforeDelete(Doc doc) {
        ProjectMemberUtils.checkDeveloper(projectService.get(doc.getProjectId()), getCurrentUser());
    }
}
