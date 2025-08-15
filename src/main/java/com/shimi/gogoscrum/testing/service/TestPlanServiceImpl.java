package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.testing.model.TestPlan;
import com.shimi.gogoscrum.testing.model.TestPlanFilter;
import com.shimi.gogoscrum.testing.model.TestRun;
import com.shimi.gogoscrum.testing.repository.TestPlanItemRepository;
import com.shimi.gogoscrum.testing.repository.TestPlanRepository;
import com.shimi.gogoscrum.testing.repository.TestPlanSpecs;
import com.shimi.gsf.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TestPlanServiceImpl extends BaseServiceImpl<TestPlan, TestPlanFilter> implements TestPlanService {
    private static final Logger log = LoggerFactory.getLogger(TestPlanServiceImpl.class);
    @Autowired
    private TestPlanRepository repository;
    @Autowired
    private TestPlanItemRepository itemRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TestPlanItemService testPlanItemService;

    /**
     * Soft delete a test plan by marking it as deleted. Because the test plan may have associated test cases,
     * test reports, and test runs, we do not delete it from the database.
     */
    @Override
    public void delete(Long id) {
        TestPlan testPlan = get(id);
        ProjectMemberUtils.checkDeveloper(projectService.get(testPlan.getProjectId()), getCurrentUser());
        testPlan.setDeleted(true);
        testPlan.setUpdateTraceInfo(getCurrentUser());
        repository.save(testPlan);
        log.info("Soft deleted test plan: {}", testPlan);
    }

    @Override
    public TestPlan clone(long testPlanId) {
        TestPlan originalTestPlan = get(testPlanId);
        ProjectMemberUtils.checkDeveloper(projectService.get(originalTestPlan.getProjectId()), getCurrentUser());

        TestPlan clonedTestPlan = new TestPlan();
        BeanUtils.copyProperties(originalTestPlan, clonedTestPlan);
        clonedTestPlan.setCaseCount(0L);
        clonedTestPlan.setExecutedCount(0L);
        clonedTestPlan.setSuccessCount(0L);
        clonedTestPlan.setFailedCount(0L);
        clonedTestPlan.setBlockedCount(0L);
        clonedTestPlan.setSkippedCount(0L);
        clonedTestPlan.setName("Copy of " + originalTestPlan.getName());

        this.create(clonedTestPlan);

        // Link all items from the original test plan to the cloned one
        List<Long> linkedCaseIds = testPlanItemService.findTestCaseIds(testPlanId);

        if (!CollectionUtils.isEmpty(linkedCaseIds)) {
            testPlanItemService.linkAll(clonedTestPlan.getId(), linkedCaseIds);
        }

        log.info("Linked {} test cases to cloned test plan: {}", linkedCaseIds.size(), clonedTestPlan.getId());

        return clonedTestPlan;
    }

    @Override
    public void refreshSummary(long testPlanId) {
        List<Object[]> rawResults = itemRepository.countByLatestRunStatus(testPlanId);
        Map<TestRun.TestRunStatus, Long> statusMap = rawResults.stream().collect(
                Collectors.toMap(arr -> (TestRun.TestRunStatus) arr[0], arr -> (Long) arr[1]));
        TestPlan testPlan = get(testPlanId);
        testPlan.setCaseCount(itemRepository.countByTestPlanId(testPlanId));
        testPlan.setSuccessCount(statusMap.getOrDefault(TestRun.TestRunStatus.SUCCESS, 0L));
        testPlan.setFailedCount(statusMap.getOrDefault(TestRun.TestRunStatus.FAILED, 0L));
        testPlan.setBlockedCount(statusMap.getOrDefault(TestRun.TestRunStatus.BLOCKED, 0L));
        testPlan.setSkippedCount(statusMap.getOrDefault(TestRun.TestRunStatus.SKIPPED, 0L));
        testPlan.setExecutedCount(testPlan.getSuccessCount() + testPlan.getFailedCount()
                + testPlan.getBlockedCount() + testPlan.getSkippedCount());
        repository.save(testPlan);
        log.info("Refreshed test plan statistics: {}", testPlan);
    }

    @Override
    protected void beforeCreate(TestPlan testPlan) {
        this.validateTestPlan(testPlan);
        ProjectMemberUtils.checkDeveloper(projectService.get(testPlan.getProjectId()), getCurrentUser());
    }

    @Override
    protected void beforeUpdate(Long id, TestPlan oldTestPlan, TestPlan newTestPlan) {
        this.validateTestPlan(newTestPlan);
        ProjectMemberUtils.checkDeveloper(projectService.get(newTestPlan.getProjectId()), getCurrentUser());

        // Ensure the test plan is not deleted before updating
        if (oldTestPlan.isDeleted()) {
            throw new BadRequestException("Cannot update a deleted test plan");
        }
    }

    @Override
    protected void beforeDelete(TestPlan testPlan) {
        ProjectMemberUtils.checkDeveloper(projectService.get(testPlan.getProjectId()), getCurrentUser());
    }

    private void validateTestPlan(TestPlan testPlan) {
        if (testPlan.getProjectId() == null) {
            throw new BadRequestException("Project ID must be provided for the test run");
        }

        if (testPlan.getName() == null || testPlan.getName().isEmpty()) {
            throw new BadRequestException("Test plan name must be provided");
        }
    }

    @Override
    protected Specification<TestPlan> toSpec(TestPlanFilter filter) {
        Specification<TestPlan> querySpec = null;

        if (filter.getProjectId() != null) {
            querySpec = TestPlanSpecs.projectIdEquals(filter.getProjectId());
        } else {
            throw new BadRequestException("Project ID is required to query test plans");
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            String keyword = filter.getKeyword();
            Specification<TestPlan> nameLike = TestPlanSpecs.nameLike(keyword);
            querySpec = querySpec.and(nameLike);
        }

        if (filter.getDeleted() != null) {
            Specification<TestPlan> deletedEquals = TestPlanSpecs.deletedEquals(filter.getDeleted());
            querySpec = querySpec.and(deletedEquals);
        }

        if (!CollectionUtils.isEmpty(filter.getTypes())) {
            Specification<TestPlan> typeIn = TestPlanSpecs.typeIn(filter.getTypes());
            querySpec = querySpec.and(typeIn);
        }

        if (!CollectionUtils.isEmpty(filter.getOwners())) {
            Specification<TestPlan> ownerIdIn = TestPlanSpecs.ownerIdIn(filter.getOwners());
            querySpec = querySpec.and(ownerIdIn);
        }

        if (!CollectionUtils.isEmpty(filter.getCreators())) {
            Specification<TestPlan> creatorIdIn = TestPlanSpecs.creatorIdIn(filter.getCreators());
            querySpec = querySpec.and(creatorIdIn);
        }

        return querySpec;
    }

    @Override
    protected TestPlanRepository getRepository() {
        return repository;
    }
}
