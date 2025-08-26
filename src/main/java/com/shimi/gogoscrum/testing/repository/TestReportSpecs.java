package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestReport;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class TestReportSpecs {
    private TestReportSpecs() {
    }

    public static Specification<TestReport> nameLike(String keyword) {
        return (testReport, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.like(testReport.get("name"), "%" + keyword + "%");
    }

    public static Specification<TestReport> projectIdEquals(Long projectId) {
        return (testReport, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(testReport.get("projectId"), projectId);
    }

    public static Specification<TestReport> planIdEquals(Long testPlanId) {
        return (testReport, criteriaQuery, criteriaBuilder)
                -> criteriaBuilder.equal(testReport.get("testPlan").get("id"), testPlanId);
    }

    public static Specification<TestReport> planIdIn(List<Long> planIds) {
        return (testReport, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testReport.get("testPlan").get("id")).value(planIds);
    }

    public static Specification<TestReport> creatorIdIn(List<Long> creatorIds) {
        return (testReport, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testReport.get("createdBy").get("id")).value(creatorIds);
    }
}
