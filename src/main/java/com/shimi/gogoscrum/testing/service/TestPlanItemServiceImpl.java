package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.testing.model.TestCase;
import com.shimi.gogoscrum.testing.model.TestPlan;
import com.shimi.gogoscrum.testing.model.TestPlanItem;
import com.shimi.gogoscrum.testing.model.TestPlanItemFilter;
import com.shimi.gogoscrum.testing.repository.TestCaseSpecs;
import com.shimi.gogoscrum.testing.repository.TestPlanItemRepository;
import com.shimi.gogoscrum.testing.repository.TestPlanItemSpecs;
import com.shimi.gsf.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TestPlanItemServiceImpl extends BaseServiceImpl<TestPlanItem, TestPlanItemFilter> implements TestPlanItemService {
    private static final Logger log = LoggerFactory.getLogger(TestPlanItemServiceImpl.class);
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TestPlanService planService;
    @Autowired
    private TestPlanItemRepository repository;


    @Override
    public Long[] findTestCaseIds(Long testPlanId) {
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
        return items;
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
