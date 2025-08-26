package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.testing.model.TestCase;
import com.shimi.gogoscrum.testing.model.TestRun;
import com.shimi.gogoscrum.testing.model.TestType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class TestCaseSpecs {
    private TestCaseSpecs() {
    }

    public static Specification<TestCase> nameLike(String keyword) {
        return (testCase, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(testCase.get("details").get("name"),
                "%" + keyword + "%");
    }

    public static Specification<TestCase> codeEquals(Long code) {
        return (testCase, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(testCase.get("code"),
                code);
    }

    public static Specification<TestCase> projectIdEquals(Long projectId) {
        return (testCase, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(testCase.get("projectId"),
                projectId);
    }

    public static Specification<TestCase> componentIdIn(List<Long> componentIds) {
        return (testCase, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testCase.get("details").get("componentId")).value(componentIds);
    }

    public static Specification<TestCase> typeIn(List<TestType> types) {
        return (testCase, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testCase.get("details").get("type")).value(types);
    }

    public static Specification<TestCase> priorityIn(List<Priority> priorities) {
        return (testCase, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testCase.get("details").get("priority")).value(priorities);
    }

    public static Specification<TestCase> runStatusIn(List<TestRun.TestRunStatus> runStatuses) {
        return (testCase, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testCase.get("latestRun").get("status")).value(runStatuses);
    }

    public static Specification<TestCase> ownerIdIn(List<Long> ownerIds) {
        return (testCase, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testCase.get("details").get("owner").get("id")).value(ownerIds);
    }

    public static Specification<TestCase> creatorIdIn(List<Long> creatorIds) {
        return (testCase, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testCase.get("createdBy").get("id")).value(creatorIds);
    }

    public static Specification<TestCase> deletedEquals(Boolean deleted) {
        return (testCase, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(testCase.get("deleted"), deleted);
    }
}
