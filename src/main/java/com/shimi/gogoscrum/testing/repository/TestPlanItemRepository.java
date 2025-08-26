package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestPlanItem;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestPlanItemRepository extends GeneralRepository<TestPlanItem> {
    @Query("SELECT t.testCase.id FROM TestPlanItem t WHERE t.testPlanId = ?1")
    List<Long> findCaseIds(Long testPlanId);
    TestPlanItem findByTestCaseIdAndTestPlanId(Long caseId, Long planId);
    Long countByTestPlanId(Long planId);
    @Query(value = "SELECT r.status, COUNT(r) " +
           "FROM TestRun r INNER JOIN TestPlanItem i on i.latestRun.id = r.id " +
           "WHERE i.testPlanId = :planId " +
           "GROUP BY r.status")
    List<Object[]> countByLatestRunStatus(Long planId);
}