package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestPlanItem;
import org.springframework.data.jpa.domain.Specification;

public class TestPlanItemSpecs {
    private TestPlanItemSpecs() {
    }

    public static Specification<TestPlanItem> planIdEquals(Long planId) {
        return (planItem, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(planItem.get("testPlanId"), planId);
    }

    public static Specification<TestPlanItem> caseDeletedEquals(Boolean deleted) {
        return (planItem, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(planItem.get("testCase").get("deleted"), deleted);
    }

    public static Specification<TestPlanItem> caseNameLike(String keyword) {
        return (planItem, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.like(planItem.get("testCase").get("details").get("name"), "%" + keyword + "%");
    }

    public static Specification<TestPlanItem> caseCodeEquals(Long code) {
        return (planItem, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(planItem.get("testCase").get("code"), code);
    }
}