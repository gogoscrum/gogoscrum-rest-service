package com.shimi.gogoscrum.issue.repository;

import com.shimi.gogoscrum.issue.model.Issue;
import com.shimi.gogoscrum.issue.model.IssuePriority;
import com.shimi.gogoscrum.issue.model.IssueType;
import com.shimi.gogoscrum.tag.model.Tag;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import java.util.List;

public class IssueSpecs {
    private IssueSpecs() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Issue> nameLike(String keyword) {
        return (issue, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(issue.get("name"), "%" + keyword + "%");
    }

    public static Specification<Issue> typeIn(List<IssueType> types) {
        return (issue, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(issue.get("type")).value(types);
    }

    public static Specification<Issue> priorityIn(List<IssuePriority> priorities) {
        return (issue, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(issue.get("priority")).value(priorities);
    }

    public static Specification<Issue> projectIdEquals(Long projectId) {
        return (issue, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(issue.get("project").get("id"), projectId);
    }

    public static Specification<Issue> sprintIdIn(List<Long> sprintIds) {
        return (issue, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(issue.get("sprint").get("id")).value(sprintIds);
    }

    public static Specification<Issue> groupIdIn(List<Long> groupIds) {
        return (issue, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(issue.get("issueGroup").get("id")).value(groupIds);
    }

    public static Specification<Issue> componentIdIn(List<Long> componentIds) {
        return (issue, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(issue.get("component").get("id")).value(componentIds);
    }

    public static Specification<Issue> ownerIdIn(List<Long> ownerIds) {
        return (issue, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(issue.get("owner").get("id")).value(ownerIds);
    }

    public static Specification<Issue> tagIdIn(List<Long> tagIds) {
        return (issue, criteriaQuery, criteriaBuilder) -> {
            Join<Issue, Tag> join = issue.join("tags", JoinType.INNER);
            return criteriaBuilder.in(join.get("id")).value(tagIds);
        };
    }

    public static Specification<Issue> codeLike(String keyword) {
        return (issue, criteriaQuery, criteriaBuilder) -> {
            Path<String> projectCodePath = issue.get("sprint").get("project").get("code");
            Expression<String> issueCodeExpression =
                    criteriaBuilder.concat(criteriaBuilder.concat(projectCodePath, "-"), issue.get("code"));
            return criteriaBuilder.like(issueCodeExpression, "%" + keyword + "%");
        };
    }

    public static Specification<Issue> inBacklog() {
        return (issue, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.isTrue(issue.get("sprint").get("backlog"));
        };
    }
}
