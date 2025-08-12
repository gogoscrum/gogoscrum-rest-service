package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestRun;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class TestRunSpecs {
    private TestRunSpecs() {
    }

    public static Specification<TestRun> projectIdEquals(Long projectId) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("projectId"), projectId);
    }

    public static Specification<TestRun> testCaseIdEquals(Long caseId) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("testCaseId"), caseId);
    }

    public static Specification<TestRun> testCaseDetailsIdEquals(Long testCaseDetailsId) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("testCaseDetailsId"), testCaseDetailsId);
    }

    public static Specification<TestRun> statusEquals(TestRun.TestRunStatus status) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("status"), status);
    }

    public static Specification<TestRun> testPlanIdEquals(Long testPlanId) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("testPlanId"), testPlanId);
    }

    public static Specification<TestRun> versionEquals(Integer version) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("version"), version);
    }

    public static Specification<TestRun> creatorIdIn(List<Long> creatorIds) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testRun.get("createdBy").get("id")).value(creatorIds);
    }
}
