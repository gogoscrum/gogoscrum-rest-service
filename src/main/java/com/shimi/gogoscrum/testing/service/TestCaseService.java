package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.testing.model.TestCase;
import com.shimi.gogoscrum.testing.model.TestCaseFilter;
import com.shimi.gsf.core.service.GeneralService;

public interface TestCaseService extends GeneralService<TestCase, TestCaseFilter> {
    TestCase getVersion(Long id, Integer version);
    TestCase cloneTestCase(long testCaseId);
}
