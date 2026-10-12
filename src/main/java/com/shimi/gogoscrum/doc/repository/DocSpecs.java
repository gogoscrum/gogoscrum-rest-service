package com.shimi.gogoscrum.doc.repository;

import com.shimi.gogoscrum.doc.model.Doc;
import org.springframework.data.jpa.domain.Specification;

public class DocSpecs {
    private DocSpecs() {
    }

    public static Specification<Doc> projectIdEqual(Long projectId) {
        return (doc, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(doc.get("projectId"), projectId);
    }

    public static Specification<Doc> nameLike(String keyword) {
        return (doc, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(doc.get("name"), "%" + keyword + "%");
    }

    public static Specification<Doc> publicAccessEqual(Boolean publicAccess) {
        return (doc, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(doc.get("publicAccess"), publicAccess);
    }

    public static Specification<Doc> creatorIdEquals(Long userId) {
        return (doc, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(doc.get("createdBy").get("id"), userId);
        };
    }
}
