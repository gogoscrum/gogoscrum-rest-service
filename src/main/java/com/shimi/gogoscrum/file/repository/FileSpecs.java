package com.shimi.gogoscrum.file.repository;

import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.file.model.FileType;
import com.shimi.gogoscrum.file.model.TargetType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

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

    public static Specification<File> targetTypeIn(List<TargetType> targetTypes) {
        return (file, criteriaQuery, criteriaBuilder) -> criteriaBuilder.in(file.get("targetType")).value(targetTypes);
    }

    public static Specification<File> fileTypeIn(List<FileType> fileTypes) {
        return (file, criteriaQuery, criteriaBuilder) -> criteriaBuilder.in(file.get("type")).value(fileTypes);
    }

    public static Specification<File> isFolder(Boolean isFolder) {
        return (file, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(file.get("folder"), isFolder);
    }

    public static Specification<File> creatorIdEquals(Long userId) {
        return (file, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.equal(file.get("createdBy").get("id"), userId);
        };
    }

    public static Specification<File> creatorIdIn(List<Long> userIds) {
        return (file, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.in(file.get("createdBy").get("id")).value(userIds);
        };
    }
}
