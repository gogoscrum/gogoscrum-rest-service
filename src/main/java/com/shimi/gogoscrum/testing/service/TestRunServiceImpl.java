package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.testing.model.TestRun;
import com.shimi.gogoscrum.testing.model.TestRunFilter;
import com.shimi.gogoscrum.testing.repository.TestRunRepository;
import com.shimi.gogoscrum.testing.repository.TestRunSpecs;
import com.shimi.gsf.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class TestRunServiceImpl extends BaseServiceImpl<TestRun, TestRunFilter> implements TestRunService {
    private static final Logger log = LoggerFactory.getLogger(TestRunServiceImpl.class);
    @Autowired
    private TestRunRepository repository;
    @Autowired
    private ProjectService projectService;

    @Override
    protected void beforeCreate(TestRun testRun) {
        this.validateTestRun(testRun);
        ProjectMemberUtils.checkDeveloper(projectService.get(testRun.getProjectId()), getCurrentUser());
    }

    @Override
    protected void beforeUpdate(Long id, TestRun oldTestRun, TestRun newTestRun) {
        this.validateTestRun(newTestRun);
        ProjectMemberUtils.checkDeveloper(projectService.get(newTestRun.getProjectId()), getCurrentUser());
    }

    @Override
    protected void beforeDelete(TestRun testRun) {
        ProjectMemberUtils.checkDeveloper(projectService.get(testRun.getProjectId()), getCurrentUser());
    }

    private void validateTestRun(TestRun testRun) {
        if (testRun.getProjectId() == null) {
            throw new BadRequestException("Project ID must be provided for the test run");
        }

        if (testRun.getTestCase() == null || testRun.getTestCaseDetailsId() == null
                || testRun.getTestCaseVersion() == null) {
            throw new BadRequestException("Test Case, Details ID and version must be all provided for the test run");
        }

        if (testRun.getStatus() == null) {
            throw new BadRequestException("Test run status must be provided");
        }
    }

    @Override
    protected Specification<TestRun> toSpec(TestRunFilter filter) {
        Specification<TestRun> querySpec = TestRunSpecs.caseDeletedEquals(Boolean.FALSE);

        if (filter.getProjectId() != null) {
            querySpec = querySpec.and(TestRunSpecs.projectIdEquals(filter.getProjectId()));
        } else {
            throw new BadRequestException("Project ID is required to query test runs");
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            String keyword = filter.getKeyword();
            Specification<TestRun> nameLikeOrCodeEquals = TestRunSpecs.caseNameLike(keyword);

            try {
                // Try to parse the keyword as a Long to match it as a code
                Long code = Long.valueOf(keyword);
                nameLikeOrCodeEquals = TestRunSpecs.caseCodeEquals(code).or(nameLikeOrCodeEquals);
            } catch (NumberFormatException e) {
                // If it is not a number, we will not try to match it as a code
            }

            querySpec = querySpec.and(nameLikeOrCodeEquals);
        }

        if (filter.getCaseId() != null) {
            Specification<TestRun> testCaseIdEquals = TestRunSpecs.caseIdEquals(filter.getCaseId());
            querySpec = querySpec.and(testCaseIdEquals);
        }

        if (filter.getCaseDetailsId() != null) {
            Specification<TestRun> testCaseDetailsIdEquals = TestRunSpecs.caseDetailsIdEquals(filter.getCaseDetailsId());
            querySpec = querySpec.and(testCaseDetailsIdEquals);
        }

        if (filter.getVersion() != null) {
            Specification<TestRun> versionEquals = TestRunSpecs.caseVersionEquals(filter.getVersion());
            querySpec = querySpec.and(versionEquals);
        }

        if (filter.getStatus() != null) {
            Specification<TestRun> statusEquals = TestRunSpecs.statusEquals(filter.getStatus());
            querySpec = querySpec.and(statusEquals);
        } else if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            Specification<TestRun> statusIn = TestRunSpecs.statusIn(filter.getStatuses());
            querySpec = querySpec.and(statusIn);
        }

        if (filter.getPlanId() != null) {
            Specification<TestRun> testPlanIdEquals = TestRunSpecs.planIdEquals(filter.getPlanId());
            querySpec = querySpec.and(testPlanIdEquals);
        } else if (!CollectionUtils.isEmpty(filter.getPlanIds())) {
            Specification<TestRun> planIdIn = TestRunSpecs.planIdIn(filter.getPlanIds());
            querySpec = querySpec.and(planIdIn);
        }

        if (!CollectionUtils.isEmpty(filter.getCreators())) {
            Specification<TestRun> creatorIdIn = TestRunSpecs.creatorIdIn(filter.getCreators());
            querySpec = querySpec.and(creatorIdIn);
        }

        return querySpec;
    }

    @Override
    protected TestRunRepository getRepository() {
        return repository;
    }
}
