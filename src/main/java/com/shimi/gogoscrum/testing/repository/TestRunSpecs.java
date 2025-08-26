package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestPlanItem;
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

    public static Specification<TestRun> caseDeletedEquals(Boolean deleted) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("testCase").get("deleted"), deleted);
    }

    public static Specification<TestRun> caseNameLike(String keyword) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(testRun.get("testCase").get("details").get("name"),
                        "%" + keyword + "%");
    }

    public static Specification<TestRun> caseCodeEquals(Long code) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("testCase").get("code"), code);
    }

    public static Specification<TestRun> caseIdEquals(Long caseId) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("testCase").get("id"), caseId);
    }

    public static Specification<TestRun> caseDetailsIdEquals(Long testCaseDetailsId) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("testCaseDetailsId"), testCaseDetailsId);
    }

    public static Specification<TestRun> caseVersionEquals(Integer version) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("version"), version);
    }

    public static Specification<TestRun> statusEquals(TestRun.TestRunStatus status) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("status"), status);
    }

    public static Specification<TestRun> statusIn(List<TestRun.TestRunStatus> statuses) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testRun.get("status")).value(statuses);
    }

    public static Specification<TestRun> planIdEquals(Long testPlanId) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(testRun.get("testPlan").get("id"), testPlanId);
    }

    public static Specification<TestRun> planIdIn(List<Long> planIds) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testRun.get("testPlan").get("id")).value(planIds);
    }

    public static Specification<TestRun> creatorIdIn(List<Long> creatorIds) {
        return (testRun, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testRun.get("createdBy").get("id")).value(creatorIds);
    }
}
