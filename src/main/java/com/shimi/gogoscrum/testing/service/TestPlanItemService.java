package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.testing.model.TestPlanItem;
import com.shimi.gogoscrum.testing.model.TestPlanItemFilter;
import com.shimi.gsf.core.service.GeneralService;

import java.util.List;

public interface TestPlanItemService extends GeneralService<TestPlanItem, TestPlanItemFilter> {
    /**
     * Retrieves the IDs of all test cases linked to a specific test plan.
     *
     * @param testPlanId the ID of the test plan
     * @return a list of Long representing the IDs of the linked test cases
     */
    List<Long> findTestCaseIds(Long testPlanId);

    /**
     * Links all test cases to a test plan.
     *
     * @param planId  the ID of the test plan
     * @param caseIds the list of test case IDs to link
     * @return a list of TestPlanItem objects representing the linked test cases
     */
    List<TestPlanItem> linkAll(Long planId, List<Long> caseIds);
}
