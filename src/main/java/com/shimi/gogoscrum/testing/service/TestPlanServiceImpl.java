package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.testing.model.TestPlan;
import com.shimi.gogoscrum.testing.model.TestPlanFilter;
import com.shimi.gogoscrum.testing.repository.TestPlanRepository;
import com.shimi.gogoscrum.testing.repository.TestPlanSpecs;
import com.shimi.gsf.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class TestPlanServiceImpl extends BaseServiceImpl<TestPlan, TestPlanFilter> implements TestPlanService {
    private static final Logger log = LoggerFactory.getLogger(TestPlanServiceImpl.class);
    @Autowired
    private TestPlanRepository repository;
    @Autowired
    private ProjectService projectService;

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
