package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestPlan;
import com.shimi.gogoscrum.testing.model.TestType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

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

    public static Specification<TestPlan> typeIn(List<TestType> types) {
        return (testPlan, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testPlan.get("type")).value(types);
    }

    public static Specification<TestPlan> ownerIdIn(List<Long> ownerIds) {
        return (testPlan, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testPlan.get("owner").get("id")).value(ownerIds);
    }

    public static Specification<TestPlan> creatorIdIn(List<Long> creatorIds) {
        return (testPlan, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(testPlan.get("createdBy").get("id")).value(creatorIds);
    }
}
