package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.testing.model.TestRun;
import com.shimi.gogoscrum.testing.model.TestRunFilter;
import com.shimi.gsf.core.service.GeneralService;

public interface TestRunService extends GeneralService<TestRun, TestRunFilter> {
    TestRun clone(long testRunId);
}
