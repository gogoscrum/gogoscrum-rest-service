package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.testing.model.*;
import com.shimi.gogoscrum.testing.repository.*;
import com.shimi.gsf.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TestReportServiceImpl extends BaseServiceImpl<TestReport, TestReportFilter> implements TestReportService {
    private static final Logger log = LoggerFactory.getLogger(TestReportServiceImpl.class);
    @Autowired
    private TestReportRepository repository;
    @Autowired
    private TestPlanService testPlanService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TestRunRepository testRunRepository;

    @Override
    protected Specification<TestReport> toSpec(TestReportFilter filter) {
        Specification<TestReport> querySpec = null;

        if (filter.getProjectId() != null) {
            querySpec = TestReportSpecs.projectIdEquals(filter.getProjectId());
        } else {
            throw new BadRequestException("Project ID is required to query test reports");
        }

        if (filter.getPlanId() != null) {
            Specification<TestReport> testPlanEquals = TestReportSpecs.planIdEquals(filter.getPlanId());
            querySpec = querySpec.and(testPlanEquals);
        } else if (!CollectionUtils.isEmpty(filter.getPlanIds())) {
            Specification<TestReport> planIdIn = TestReportSpecs.planIdIn(filter.getPlanIds());
            querySpec = querySpec.and(planIdIn);
        }

        if (!CollectionUtils.isEmpty(filter.getCreators())) {
            Specification<TestReport> creatorIdIn = TestReportSpecs.creatorIdIn(filter.getCreators());
            querySpec = querySpec.and(creatorIdIn);
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            String keyword = filter.getKeyword();
            Specification<TestReport> nameLike = TestReportSpecs.nameLike(keyword);
            querySpec = querySpec.and(nameLike);
        }

        return querySpec;
    }

    @Override
    public TestReport generateReport(Long testPlanId) {
        TestPlan testPlan = testPlanService.get(testPlanId);

        if (testPlan == null) {
            throw new BadRequestException("Test plan not found for ID: " + testPlanId);
        }

        ProjectMemberUtils.checkDeveloper(projectService.get(testPlan.getProjectId()), getCurrentUser());

        TestReport report = new TestReport();
        report.setProjectId(testPlan.getProjectId());
        report.setTestPlan(testPlan);
        report.setStartDate(testPlan.getStartDate());
        report.setEndDate(testPlan.getEndDate());
        report.setOwner(testPlan.getOwner());
        report.setCaseSummary(this.getCaseSummary(testPlan));
        report.setBugSummary(this.getBugSummary(testPlanId));

        return report;
    }

    private TestReport.CaseSummary getCaseSummary(TestPlan plan) {
        Long planId = plan.getId();
        TestReport.CaseSummary summary = new TestReport.CaseSummary();
        summary.setCaseIds(repository.findCaseIds(planId));
        summary.setCaseCount(plan.getCaseCount());
        summary.setExecutedCaseCount(plan.getExecutedCount());
        summary.setExecutionRecordCount(testRunRepository.countByTestPlanId(planId));
        summary.setCaseByStatusSummary(this.countCaseByStatus(plan));
        summary.setCaseByComponentSummary(this.countCaseByComponent(planId));
        summary.setCaseByTypeSummary(this.countCaseByType(planId));
        summary.setCaseByExecutorSummary(this.countCaseByExecutor(planId));
        return summary;
    }

    private TestReport.BugSummary getBugSummary(Long planId) {
        TestReport.BugSummary bugSummary = new TestReport.BugSummary();

        bugSummary.setBugIds(repository.findBugIds(planId));
        bugSummary.setBugCount((long) bugSummary.getBugIds().size());
        bugSummary.setBugByPrioritySummary(this.countBugByPriority(planId));
        bugSummary.setBugByStatusSummary(this.countBugByStatus(planId));
        bugSummary.setBugByCreatorSummary(this.countBugByCreator(planId));
        bugSummary.setBugByAssigneeSummary(this.countBugByAssignee(planId));
        bugSummary.setBugByComponentSummary(this.countBugByComponent(planId));

        return bugSummary;
    }

    private LinkedHashMap<TestRun.TestRunStatus, Long> countCaseByStatus(TestPlan plan) {
        Map<TestRun.TestRunStatus, Long> map = Map.of(TestRun.TestRunStatus.SUCCESS, plan.getSuccessCount(),
                TestRun.TestRunStatus.FAILED, plan.getFailedCount(),
                TestRun.TestRunStatus.BLOCKED, plan.getBlockedCount(),
                TestRun.TestRunStatus.SKIPPED, plan.getSkippedCount());
        // sort the entries in the map by the ordinal value of the key enum
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey((a, b) -> Integer.compare(a.ordinal(), b.ordinal())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private LinkedHashMap<Long, Long> countCaseByComponent(Long planId) {
        List<Object[]> rawResults = repository.countCaseByComponent(planId);
        Map<Long, Long> map = rawResults.stream().collect(
                Collectors.toMap(arr -> arr[0] == null ? 0L : (Long) arr[0],
                        arr -> (Long) arr[1]));
        // sort the entries in the map by the key (component ID) descending
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey((a, b) -> Long.compare(b, a)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private LinkedHashMap<TestType, Long> countCaseByType(Long planId) {
        List<Object[]> rawResults = repository.countCaseByType(planId);
        Map<TestType, Long> map = rawResults.stream().collect(
                Collectors.toMap(arr -> arr[0] == null ? TestType.NOT_SET : (TestType) arr[0],
                        arr -> (Long) arr[1]));
        // sort the entries in the map by the ordinal value of the key enum
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey((a, b) -> Integer.compare(a.ordinal(), b.ordinal())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private LinkedHashMap<Long, Long> countCaseByExecutor(Long planId) {
        List<Object[]> rawResults = repository.countCaseByExecutor(planId);
        Map<Long, Long> map = rawResults.stream().collect(
                Collectors.toMap(arr -> arr[0] == null ? 0L : (Long) arr[0],
                        arr -> (Long) arr[1]));
        // sort the entries in the map by the key (user ID) descending
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey((a, b) -> Long.compare(b, a)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private LinkedHashMap<Priority, Long> countBugByPriority(Long planId) {
        List<Object[]> rawResults = repository.countBugByPriority(planId);
        return rawResults.stream().collect(
                Collectors.toMap(arr -> (Priority) arr[0],
                        arr -> (Long) arr[1],
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private LinkedHashMap<String, Long> countBugByStatus(Long planId) {
        List<Object[]> rawResults = repository.countBugByStatus(planId);
        return rawResults.stream().collect(
                Collectors.toMap(arr -> (String) arr[0],
                        arr -> (Long) arr[1],
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private LinkedHashMap<Long, Long> countBugByCreator(Long planId) {
        List<Object[]> rawResults = repository.countBugByCreator(planId);
        return rawResults.stream().collect(
                Collectors.toMap(arr -> (Long) arr[0],
                        arr -> (Long) arr[1], (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private LinkedHashMap<Long, Long> countBugByAssignee(Long planId) {
        List<Object[]> rawResults = repository.countBugByAssignee(planId);
        return rawResults.stream().collect(
                Collectors.toMap(arr -> arr[0] == null ? 0L : (Long) arr[0],
                        arr -> (Long) arr[1], (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private LinkedHashMap<Long, Long> countBugByComponent(Long planId) {
        List<Object[]> rawResults = repository.countBugByComponent(planId);
        return rawResults.stream().collect(
                Collectors.toMap(arr -> arr[0] == null ? 0L : (Long) arr[0],
                        arr -> (Long) arr[1], (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    @Override
    protected TestReportRepository getRepository() {
        return repository;
    }
}
