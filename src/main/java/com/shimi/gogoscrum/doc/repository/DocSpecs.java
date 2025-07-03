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
}
