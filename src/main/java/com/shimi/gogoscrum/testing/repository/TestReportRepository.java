package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestReport;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestReportRepository extends GeneralRepository<TestReport> {
    // Test case related queries
    @Query(value = "SELECT i.testCase.id FROM TestPlanItem i WHERE i.testPlanId = :planId")
    List<Long> findCaseIds(Long planId);

    @Query(value = "SELECT d.componentId, COUNT(d) " +
            "FROM TestCaseDetails d INNER JOIN TestPlanItem i on d.testCaseId = i.testCase.id " +
            "INNER JOIN TestCase c ON c.details.id = d.id " +
            "WHERE i.testPlanId = :planId " +
            "GROUP BY d.componentId " +
            "ORDER BY CASE WHEN d.componentId IS NULL THEN 1 ELSE 0 END, d.componentId")
    List<Object[]> countCaseByComponent(Long planId);

    @Query(value = "SELECT d.type, COUNT(d) " +
            "FROM TestCaseDetails d INNER JOIN TestPlanItem i on d.testCaseId = i.testCase.id " +
            "INNER JOIN TestCase c ON c.details.id = d.id " +
            "WHERE i.testPlanId = :planId " +
            "GROUP BY d.type " +
            "ORDER BY CASE WHEN d.type IS NULL THEN 1 ELSE 0 END")
    List<Object[]> countCaseByType(Long planId);

    @Query(value = "SELECT r.createdBy.id, COUNT(c) " +
            "FROM TestRun r INNER JOIN TestCase c on c.latestRun.id = r.id " +
            "INNER JOIN TestPlanItem i on c.id = i.testCase.id " +
            "WHERE i.testPlanId = :planId AND r.testPlan.id = i.testPlanId " +
            "GROUP BY r.createdBy.id")
    List<Object[]> countCaseByExecutor(Long planId);

    // Bug related queries
    @Query(value = "SELECT i.id FROM Issue i WHERE i.testPlan.id = :planId AND i.type = 'BUG'")
    List<Long> findBugIds(Long planId);

    @Query(value = "SELECT i.priority, COUNT(i) FROM Issue i WHERE i.testPlan.id = :planId " +
            "GROUP BY i.priority ORDER BY i.priority DESC")
    List<Object[]> countBugByPriority(Long planId);

    @Query(value = "SELECT i.issueGroup.label, COUNT(i) FROM Issue i WHERE i.testPlan.id = :planId " +
            "GROUP BY i.issueGroup.label, i.issueGroup.seq ORDER BY i.issueGroup.seq")
    List<Object[]> countBugByStatus(Long planId);

    @Query(value = "SELECT i.createdBy.id, COUNT(i) FROM Issue i WHERE i.testPlan.id = :planId " +
            "GROUP BY i.createdBy.id")
    List<Object[]> countBugByCreator(Long planId);

    @Query(value = "SELECT i.owner.id, COUNT(i) FROM Issue i WHERE i.testPlan.id = :planId " +
            "GROUP BY i.owner.id")
    List<Object[]> countBugByAssignee(Long planId);

    @Query(value = "SELECT i.component.id, COUNT(i) FROM Issue i WHERE i.testPlan.id = :planId " +
            "GROUP BY i.component.id")
    List<Object[]> countBugByComponent(Long planId);
}