package com.shimi.gogoscrum.project.repository;

import com.shimi.gogoscrum.project.model.Project;
import com.shimi.gogoscrum.project.model.ProjectMember;
import com.shimi.gogoscrum.user.model.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class ProjectSpecs {
    private ProjectSpecs() {
    }

    public static Specification<Project> nameLike(String keyword) {
        return (project, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(project.get("name"),
                "%" + keyword + "%");
    }

    public static Specification<Project> codeLike(String keyword) {
        return (project, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(project.get("code"),
                "%" + keyword + "%");
    }

    public static Specification<Project> deletedEquals(Boolean deleted) {
        return (project, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(project.get("deleted"), deleted);
    }

    public static Specification<Project> archivedEquals(Boolean archived) {
        return (project, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(project.get("archived"), archived);
    }

    public static Specification<Project> isProjectMember(User user) {
        return (project, criteriaQuery, criteriaBuilder) ->{
                Join<Project, ProjectMember> join = project.join("projectMembers", JoinType.INNER);
                return criteriaBuilder.equal(join.get("user").get("id"), user.getId());
        };
    }
}
