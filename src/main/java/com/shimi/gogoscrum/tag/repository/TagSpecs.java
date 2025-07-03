package com.shimi.gogoscrum.tag.repository;

import com.shimi.gogoscrum.tag.model.Tag;
import org.springframework.data.jpa.domain.Specification;

public class TagSpecs {
    private TagSpecs() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Tag> nameLike(String keyword) {
        return (tag, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(tag.get("name"), "%" + keyword + "%");
    }

    public static Specification<Tag> projectIdEquals(Long projectId) {
        return (tag, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(tag.get("projectId"), projectId);
    }
}
