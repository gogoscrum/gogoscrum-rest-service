package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.testing.model.TestPlan;
import com.shimi.gogoscrum.testing.model.TestPlanFilter;
import com.shimi.gsf.core.service.GeneralService;

public interface TestPlanService extends GeneralService<TestPlan, TestPlanFilter> {
    TestPlan clone(long testPlanId);

    /**
     * Refreshes the summary of a test plan by recalculating its statistics, including
     * the total number of test cases, excuted test cases, and the number of passed and failed test cases.
     *
     * @param testPlanId the ID of the test plan to refresh
     */
    void refreshSummary(long testPlanId);
}
