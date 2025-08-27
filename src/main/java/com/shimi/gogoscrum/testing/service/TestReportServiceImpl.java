package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.common.model.Priority;
import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.testing.model.*;
import com.shimi.gogoscrum.testing.repository.TestReportRepository;
import com.shimi.gogoscrum.testing.repository.TestReportSpecs;
import com.shimi.gogoscrum.testing.repository.TestRunRepository;
import com.shimi.gsf.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

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

    private List<TestReport.SummaryEntry> countCaseByStatus(TestPlan plan) {
        return List.of(new TestReport.SummaryEntry(TestRun.TestRunStatus.SUCCESS.name(), plan.getSuccessCount()),
                new TestReport.SummaryEntry(TestRun.TestRunStatus.FAILED.name(), plan.getFailedCount()),
                new TestReport.SummaryEntry(TestRun.TestRunStatus.BLOCKED.name(), plan.getBlockedCount()),
                new TestReport.SummaryEntry(TestRun.TestRunStatus.SKIPPED.name(), plan.getSkippedCount()));
    }

    private List<TestReport.SummaryEntry> countCaseByComponent(Long planId) {
        List<Object[]> rawResults = repository.countCaseByComponent(planId);
        return rawResults.stream().map(arr ->
                        new TestReport.SummaryEntry(arr[0] == null ? "0" : String.valueOf(arr[0]), (Long) arr[1]))
                .toList();
    }

    private List<TestReport.SummaryEntry> countCaseByType(Long planId) {
        List<Object[]> rawResults = repository.countCaseByType(planId);
        return rawResults.stream().map(arr ->
                new TestReport.SummaryEntry(arr[0] == null ? TestType.NOT_SET.name() : ((TestType) arr[0]).name(),
                        (Long) arr[1])).toList();
    }

    private List<TestReport.SummaryEntry> countCaseByExecutor(Long planId) {
        List<Object[]> rawResults = repository.countCaseByExecutor(planId);
        return rawResults.stream().map(arr ->
                new TestReport.SummaryEntry(String.valueOf(arr[0]), (Long) arr[1])).toList();
    }

    private List<TestReport.SummaryEntry> countBugByPriority(Long planId) {
        List<Object[]> rawResults = repository.countBugByPriority(planId);
        return rawResults.stream().map(arr ->
                new TestReport.SummaryEntry(((Priority) arr[0]).name(), (Long) arr[1])).toList();
    }

    private List<TestReport.SummaryEntry> countBugByStatus(Long planId) {
        List<Object[]> rawResults = repository.countBugByStatus(planId);
        return rawResults.stream().map(arr ->
                new TestReport.SummaryEntry((String) arr[0], (Long) arr[1])).toList();
    }

    private List<TestReport.SummaryEntry> countBugByCreator(Long planId) {
        List<Object[]> rawResults = repository.countBugByCreator(planId);
        return rawResults.stream().map(arr ->
                new TestReport.SummaryEntry(String.valueOf(arr[0]), (Long) arr[1])).toList();
    }

    private List<TestReport.SummaryEntry> countBugByAssignee(Long planId) {
        List<Object[]> rawResults = repository.countBugByAssignee(planId);
        return rawResults.stream().map(arr ->
                new TestReport.SummaryEntry(String.valueOf(arr[0]), (Long) arr[1])).toList();
    }

    private List<TestReport.SummaryEntry> countBugByComponent(Long planId) {
        List<Object[]> rawResults = repository.countBugByComponent(planId);
        return rawResults.stream().map(arr ->
                new TestReport.SummaryEntry(String.valueOf(arr[0]), (Long) arr[1])).toList();
    }

    @Override
    protected TestReportRepository getRepository() {
        return repository;
    }

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
}
