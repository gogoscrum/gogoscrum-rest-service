package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.common.service.BaseServiceImpl;
import com.shimi.gogoscrum.project.service.ProjectService;
import com.shimi.gogoscrum.project.utils.ProjectMemberUtils;
import com.shimi.gogoscrum.testing.model.TestCase;
import com.shimi.gogoscrum.testing.model.TestCaseDetails;
import com.shimi.gogoscrum.testing.model.TestCaseFilter;
import com.shimi.gogoscrum.testing.repository.TestCaseDetailsRepository;
import com.shimi.gogoscrum.testing.repository.TestCaseRepository;
import com.shimi.gogoscrum.testing.repository.TestCaseSpecs;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gsf.core.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class TestCaseServiceImpl extends BaseServiceImpl<TestCase, TestCaseFilter> implements TestCaseService {
    private static final Logger log = LoggerFactory.getLogger(TestCaseServiceImpl.class);
    @Autowired
    private TestCaseRepository repository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TestCaseDetailsRepository detailsRepository;

    @Override
    protected TestCaseRepository getRepository() {
        return repository;
    }

    @Override
    public TestCase create(TestCase testCase) {
        User currentUser = getCurrentUser();
        ProjectMemberUtils.checkDeveloper(projectService.get(testCase.getProjectId()), currentUser);
        this.validateTestCase(testCase);

        // Save the test case
        testCase.setCode(this.generateNextCaseCode(testCase.getProjectId()));
        TestCase createdTestCase = super.create(testCase);

        // Save the test case details
        TestCaseDetails details = testCase.getDetails();
        details.setId(null);
        details.setTestCaseId(createdTestCase.getId());
        details.setVersion(1);
        details.setAllTraceInfo(currentUser);
        TestCaseDetails savedDetails = detailsRepository.save(details);

        if (log.isDebugEnabled()) {
            log.debug("Test case details created: {}", savedDetails);
        }

        // Update the test case with the saved details
        createdTestCase.setDetails(savedDetails);
        createdTestCase.setLatestVersion(savedDetails.getVersion());
        repository.save(createdTestCase);

        return createdTestCase;
    }

    /**
     * Updates an existing test case. A new version of the test case details is created.
     */
    @Override
    public TestCase update(Long id, TestCase testCase) {
        this.validateTestCase(testCase);
        TestCase existingCase = get(id);
        User currentUser = getCurrentUser();
        ProjectMemberUtils.checkDeveloper(projectService.get(existingCase.getProjectId()), currentUser);

        if (existingCase.isDeleted()) {
            throw new BadRequestException("Cannot update a deleted test case");
        }

        TestCaseDetails details = testCase.getDetails();
        details.setId(null);
        details.setTestCaseId(id);
        details.setVersion(this.generateNextVersion(id));
        details.setAllTraceInfo(currentUser);
        TestCaseDetails savedDetails = detailsRepository.save(details);

        existingCase.setDetails(savedDetails);
        existingCase.setLatestVersion(savedDetails.getVersion());
        existingCase.setFiles(testCase.getFiles());
        existingCase.setUpdateTraceInfo(currentUser);
        TestCase updatedCase =repository.save(existingCase);
        log.info("Created new version of details for test case {}: {}", id, savedDetails);
        return updatedCase;
    }

    @Override
    public void delete(Long id) {
        TestCase testCase = get(id);
        ProjectMemberUtils.checkDeveloper(projectService.get(testCase.getProjectId()), getCurrentUser());
        testCase.setDeleted(true);
        testCase.setUpdateTraceInfo(getCurrentUser());
        repository.save(testCase);
        log.info("Test case has execution results, soft deleted: {}", testCase);
    }

    @Override
    public TestCaseDetails getDetails(Long testCaseId, Integer version) {
        TestCase testCase = get(testCaseId);
        ProjectMemberUtils.checkMember(projectService.get(testCase.getProjectId()), getCurrentUser());

        TestCaseDetails details = detailsRepository.findByTestCaseIdAndVersion(testCaseId, version);

        if (details == null) {
            throw new BadRequestException("Test case details version not found: " + version);
        } else {
            return details;
        }
    }

    private long generateNextCaseCode(Long projectId) {
        long previousCode = Objects.requireNonNullElse(repository.getMaxCode(projectId), 0L);
        return previousCode + 1;
    }

    private int generateNextVersion(long caseId) {
        Integer previousVersion = Objects.requireNonNullElse(detailsRepository.getMaxVersion(caseId), 0);
        return previousVersion + 1;
    }

    private void validateTestCase(TestCase testCase) {
        if (testCase.getProjectId() == null) {
            throw new BadRequestException("Project ID must be provided for the test case");
        }

        if (testCase.getDetails() == null) {
            throw new BadRequestException("Test case details must be provided");
        }

        if (!StringUtils.hasText(testCase.getDetails().getName())) {
            throw new BadRequestException("Test case name cannot be empty");
        }
    }

    @Override
    public TestCase clone(long testCaseId) {
        TestCase originalTestCase = get(testCaseId);
        TestCaseDetails originalDetails = originalTestCase.getDetails();

        ProjectMemberUtils.checkDeveloper(projectService.get(originalTestCase.getProjectId()), getCurrentUser());

        TestCase clonedCase = new TestCase();
        TestCaseDetails clonedDetails = new TestCaseDetails();

        BeanUtils.copyProperties(originalTestCase, clonedCase, "details", "files");
        BeanUtils.copyProperties(originalDetails, clonedDetails);

        clonedDetails.setName("Copy of " + originalDetails.getName());
        clonedCase.setDetails(clonedDetails);

        return create(clonedCase);
    }

    @Override
    protected Specification<TestCase> toSpec(TestCaseFilter filter) {
        Specification<TestCase> querySpec = null;

        if (filter.getProjectId() != null) {
            querySpec = TestCaseSpecs.projectIdEquals(filter.getProjectId());
        } else {
            throw new BadRequestException("Project ID is required to query test cases");
        }

        if (!CollectionUtils.isEmpty(filter.getComponentIds())) {
            Specification<TestCase> componentIdIn = TestCaseSpecs.componentIdIn(filter.getComponentIds());
            querySpec = querySpec.and(componentIdIn);
        }

        if (!CollectionUtils.isEmpty(filter.getTypes())) {
            Specification<TestCase> typeIn = TestCaseSpecs.typeIn(filter.getTypes());
            querySpec = querySpec.and(typeIn);
        }

        if (!CollectionUtils.isEmpty(filter.getPriorities())) {
            Specification<TestCase> priorityIn = TestCaseSpecs.priorityIn(filter.getPriorities());
            querySpec = querySpec.and(priorityIn);
        }

        if (!CollectionUtils.isEmpty(filter.getOwners())) {
            Specification<TestCase> ownerIdIn = TestCaseSpecs.ownerIdIn(filter.getOwners());
            querySpec = querySpec.and(ownerIdIn);
        }

        if (!CollectionUtils.isEmpty(filter.getCreators())) {
            Specification<TestCase> creatorIdIn = TestCaseSpecs.creatorIdIn(filter.getCreators());
            querySpec = querySpec.and(creatorIdIn);
        }

        if (StringUtils.hasText(filter.getKeyword())) {
            String keyword = filter.getKeyword();
            Specification<TestCase> codeOrNameLike = TestCaseSpecs.nameLike(keyword);

            try {
                // Try to parse the keyword as a Long to match it as a code
                Long code = Long.valueOf(keyword);
                codeOrNameLike = TestCaseSpecs.codeEquals(code).or(codeOrNameLike);
            } catch (NumberFormatException e) {
                // If it is not a number, we will not try to match it as a code
            }

            querySpec = querySpec.and(codeOrNameLike);
        }

        if (filter.getDeleted() != null) {
            Specification<TestCase> deletedEquals = TestCaseSpecs.deletedEquals(filter.getDeleted());
            querySpec = querySpec.and(deletedEquals);
        }

        return querySpec;
    }
}
