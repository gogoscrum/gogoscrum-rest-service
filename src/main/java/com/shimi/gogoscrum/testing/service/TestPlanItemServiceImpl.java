package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.testing.model.*;
import com.shimi.gogoscrum.testing.repository.TestPlanItemRepository;
import com.shimi.gogoscrum.testing.repository.TestPlanItemSpecs;
import com.shimi.gogoscrum.testing.repository.TestRunRepository;
import com.shimi.gsf.core.event.EntityChangeEvent;
import com.shimi.gsf.core.exception.BadRequestException;
import com.shimi.gsf.core.model.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class TestPlanItemServiceImpl extends BaseServiceImpl<TestPlanItem, TestPlanItemFilter> implements TestPlanItemService {
    private static final Logger log = LoggerFactory.getLogger(TestPlanItemServiceImpl.class);
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TestPlanService planService;
    @Autowired
    private TestPlanItemRepository repository;
    @Autowired
    private TestRunRepository testRunRepository;

    @Override
    public List<Long> findTestCaseIds(Long testPlanId) {
        return repository.findCaseIds(testPlanId);
    }

    @Override
    public List<TestPlanItem> linkAll(Long planId, List<Long> caseIds) {
        TestPlan plan = planService.get(planId);
        ProjectMemberUtils.checkDeveloper(projectService.get(plan.getProjectId()), getCurrentUser());

        List<TestPlanItem> items = caseIds.stream()
                .map(caseId -> {
                    TestPlanItem item = new TestPlanItem();
                    item.setTestPlanId(planId);
                    item.setTestCase(new TestCase(caseId));
                    item.setAllTraceInfo(getCurrentUser());
                    return item;
                }).toList();
        repository.saveAll(items);
        log.info("Linked {} test cases to test plan {}: {}", caseIds.size(), planId, caseIds);
        planService.refreshSummary(planId);
        return items;
    }

    @Override
    protected void afterDelete(TestPlanItem item) {
        planService.refreshSummary(item.getTestPlanId());
    }

    /**
     * Event listener to refresh the latest run of a test plan item when an execution record changes.
     */
    @EventListener
    public void onExecutionRecordChanged(EntityChangeEvent event) {
        Entity entity = Objects.requireNonNullElse(event.getPreviousEntity(), event.getUpdatedEntity());

        if (entity instanceof TestRun run && run.getTestCase() != null && run.getTestPlan() != null) {
            this.refreshItemLatestRun(run.getTestCase().getId(), run.getTestPlan().getId());
        }
    }

    /**
     * Refresh the latest run of the test plan item after an execution record change.
     * This method should be called after a test run is created, updated or deleted.
     * It updates the latest run information for the plan item (i.e. identified by case ID and plan ID).
     *
     * @param caseId the ID of the test case to refresh
     */
    private void refreshItemLatestRun(Long caseId, Long planId) {
        TestPlanItem item = repository.findByTestCaseIdAndTestPlanId(caseId, planId);
        TestRun latestRun = testRunRepository.findTopByTestCaseIdAndTestPlanIdOrderByIdDesc(caseId, planId);

        if (latestRun != null) {
            item.setLatestRun(latestRun);
            repository.save(item);
            log.info("Updated latest execution record for test plan item {}: {}", item.getId(), latestRun);
        } else {
            item.setLatestRun(null);
            repository.save(item);
            log.info("No execution record found for test plan item {}, set to null", item.getId());
        }
        planService.refreshSummary(planId);
    }

    @Override
    protected Specification<TestPlanItem> toSpec(TestPlanItemFilter filter) {
        Specification<TestPlanItem> querySpec = TestPlanItemSpecs.caseDeletedEquals(Boolean.FALSE);

        if (filter.getTestPlanId() != null) {
            Specification<TestPlanItem> planIdEquals = TestPlanItemSpecs.planIdEquals(filter.getTestPlanId());
            querySpec = querySpec.and(planIdEquals);
        } else {
            throw new BadRequestException("Test plan ID is required for filtering test plan items.");
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            String keyword = filter.getKeyword();
            Specification<TestPlanItem> codeOrNameLike = TestPlanItemSpecs.caseNameLike(keyword);

            try {
                // Try to parse the keyword as a Long to match it as a code
                Long code = Long.valueOf(keyword);
                codeOrNameLike = TestPlanItemSpecs.caseCodeEquals(code).or(codeOrNameLike);
            } catch (NumberFormatException e) {
                // If it is not a number, we will not try to match it as a code
            }

            querySpec = querySpec.and(codeOrNameLike);
        }


        return querySpec;
    }

    @Override
    protected TestPlanItemRepository getRepository() {
        return repository;
    }
}
