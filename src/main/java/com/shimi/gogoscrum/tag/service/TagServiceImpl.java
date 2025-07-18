package com.shimi.gogoscrum.tag.service;

import com.shimi.gogoscrum.common.exception.ErrorCode;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.tag.model.Tag;
import com.shimi.gogoscrum.tag.model.TagFilter;
import com.shimi.gogoscrum.tag.repository.TagRepository;
import com.shimi.gogoscrum.tag.repository.TagSpecs;
import com.shimi.gsf.core.exception.BaseServiceException;
import com.shimi.gsf.core.exception.EntityDuplicatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TagServiceImpl extends BaseServiceImpl<Tag, TagFilter> implements TagService {
    private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);
    @Autowired
    private TagRepository repository;
    @Autowired
    private ProjectService projectService;

    @Override
    protected TagRepository getRepository() {
        return repository;
    }

    @Override
    public Tag get(Long id) {
        Tag tag = super.get(id);
        ProjectMemberUtils.checkMember(projectService.get(tag.getProjectId()), getCurrentUser());
        return tag;
    }

    @Override
    protected void beforeCreate(Tag tag) {
        ProjectMemberUtils.checkDeveloper(projectService.get(tag.getProjectId()), getCurrentUser());

        Tag duplicatedTag = repository.findByNameAndProjectId(tag.getName(), tag.getProjectId());

        if(duplicatedTag != null) {
            throw new EntityDuplicatedException("Duplicated tag already exists in project");
        }
    }

    @Override
    public void beforeUpdate(Long id, Tag existingEntity, Tag newTag) {
        ProjectMemberUtils.checkDeveloper(projectService.get(existingEntity.getProjectId()), getCurrentUser());

        Tag duplicatedTag = repository.findByNameAndProjectId(newTag.getName(), newTag.getProjectId());

        if(duplicatedTag != null && !duplicatedTag.getId().equals(newTag.getId())) {
            throw new BaseServiceException(
                    ErrorCode.DUPLICATED_TAG, "Duplicated tag name already exists", HttpStatus.CONFLICT);
        }
    }

    @Override
    protected void beforeDelete(Tag tag) {
        ProjectMemberUtils.checkDeveloper(projectService.get(tag.getProjectId()), getCurrentUser());
    }

    @Override
    protected Specification<Tag> toSpec(TagFilter filter) {
        Specification<Tag> querySpec = null;

        if (filter.getProjectId() != null) {
            querySpec = TagSpecs.projectIdEquals(filter.getProjectId());
        } else {
            throw new BaseServiceException(ErrorCode.INVALID_REQUEST_DATA, "Project ID must be provided", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            Specification<Tag> nameLike = TagSpecs.nameLike(filter.getKeyword());
            querySpec = querySpec.and(nameLike);
        }

        return querySpec;
    }
}
