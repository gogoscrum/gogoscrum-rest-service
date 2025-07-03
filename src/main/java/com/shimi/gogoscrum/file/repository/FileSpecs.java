package com.shimi.gogoscrum.file.repository;

import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.model.TargetType;
import org.springframework.data.jpa.domain.Specification;

public class FileSpecs {
    private FileSpecs() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<File> projectIdEqual(Long projectId) {
        return (file, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(file.get("projectId"), projectId);
    }

    public static Specification<File> parentIdEqual(Long parentId) {
        return (file, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(file.get("parent").get("id"), parentId);
    }

    public static Specification<File> parentIdNull() {
        return (file, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isNull(file.get("parent").get("id"));
    }

    public static Specification<File> nameLike(String keyword) {
        return (file, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(file.get("name"), "%" + keyword + "%");
    }

    public static Specification<File> targetTypeEqual(TargetType targetType) {
        return (file, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(file.get("targetType"), targetType);
    }
}
