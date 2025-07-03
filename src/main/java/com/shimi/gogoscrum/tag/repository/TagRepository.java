package com.shimi.gogoscrum.tag.repository;

import com.shimi.gogoscrum.tag.model.Tag;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagRepository extends GeneralRepository<Tag> {
    Tag findByNameAndProjectId(String name, Long projectId);
    Page<Tag> findByProjectId(Long projectId, Pageable pageable);
    Page<Tag> findByProjectIdAndNameContaining(Long projectId, String name, Pageable pageable);
}
