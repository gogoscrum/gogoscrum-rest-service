package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestPlan;
import org.springframework.data.jpa.domain.Specification;

public class TestPlanSpecs {
    private TestPlanSpecs() {
    }

    public static Specification<TestPlan> nameLike(String keyword) {
        return (testPlan, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(testPlan.get("name"),
                "%" + keyword + "%");
    }

    public static Specification<TestPlan> projectIdEquals(Long projectId) {
        return (testPlan, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(testPlan.get("projectId"),
                projectId);
    }

    public static Specification<TestPlan> deletedEquals(Boolean deleted) {
        return (testPlan, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(testPlan.get("deleted"), deleted);
    }
}
