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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class TestRunServiceImpl extends BaseServiceImpl<TestRun, TestRunFilter> implements TestRunService {
    private static final Logger log = LoggerFactory.getLogger(TestRunServiceImpl.class);
    @Autowired
    private TestRunRepository repository;
    @Autowired
    private ProjectService projectService;


    /**
     * Clones a test run by copying all its properties except the attachments.
     *
     * @param testRunId the ID of the test run to clone
     * @return the cloned test run
     */
    @Override
    public TestRun clone(long testRunId) {
        TestRun originalTestRun = get(testRunId);
        ProjectMemberUtils.checkDeveloper(projectService.get(originalTestRun.getProjectId()), getCurrentUser());
        TestRun clonedRun = new TestRun();
        BeanUtils.copyProperties(originalTestRun, clonedRun, "files");
        return create(clonedRun);
    }

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

        if (testRun.getTestCaseId() == null || testRun.getTestCaseDetailsId() == null
                || testRun.getTestCaseVersion() == null) {
            throw new BadRequestException("Test Case ID, Details ID and version must be all provided for the test run");
        }

        if (testRun.getStatus() == null) {
            throw new BadRequestException("Test run status must be provided");
        }
    }

    @Override
    protected Specification<TestRun> toSpec(TestRunFilter filter) {
        Specification<TestRun> querySpec = null;

        if (filter.getProjectId() != null) {
            querySpec = TestRunSpecs.projectIdEquals(filter.getProjectId());
        } else {
            throw new BadRequestException("Project ID is required to query test runs");
        }

        if (filter.getTestCaseId() != null) {
            Specification<TestRun> testCaseIdEquals = TestRunSpecs.testCaseIdEquals(filter.getTestCaseId());
            querySpec = querySpec.and(testCaseIdEquals);
        }

        if (filter.getTestCaseDetailsId() != null) {
            Specification<TestRun> testCaseDetailsIdEquals = TestRunSpecs.testCaseDetailsIdEquals(filter.getTestCaseDetailsId());
            querySpec = querySpec.and(testCaseDetailsIdEquals);
        }

        if (filter.getStatus() != null) {
            Specification<TestRun> statusEquals = TestRunSpecs.statusEquals(filter.getStatus());
            querySpec = querySpec.and(statusEquals);
        }

        if (filter.getTestPlanId() != null) {
            Specification<TestRun> testPlanIdEquals = TestRunSpecs.testPlanIdEquals(filter.getTestPlanId());
            querySpec = querySpec.and(testPlanIdEquals);
        }

        if (filter.getVersion() != null) {
            Specification<TestRun> versionEquals = TestRunSpecs.versionEquals(filter.getVersion());
            querySpec = querySpec.and(versionEquals);
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
