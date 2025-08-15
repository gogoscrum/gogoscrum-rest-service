package com.shimi.gogoscrum.testing.repository;

import com.shimi.gogoscrum.testing.model.TestRun;
import com.shimi.gsf.core.repository.GeneralRepository;

public interface TestRunRepository extends GeneralRepository<TestRun> {
    TestRun findTopByTestCaseIdOrderByIdDesc(Long testCaseId);
    TestRun findTopByTestCaseIdAndTestPlanIdOrderByIdDesc(Long testCaseId, Long testPlanId);
}