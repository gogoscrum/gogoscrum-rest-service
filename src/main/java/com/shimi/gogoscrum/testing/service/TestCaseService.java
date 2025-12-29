package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.testing.model.TestCase;
import com.shimi.gogoscrum.testing.model.TestCaseDetails;
import com.shimi.gogoscrum.testing.model.TestCaseFilter;
import com.shimi.gsf.core.service.GeneralService;

import java.util.List;

public interface TestCaseService extends GeneralService<TestCase, TestCaseFilter> {
    TestCaseDetails getDetails(Long testCaseId, Integer version);
    TestCase clone(long testCaseId);
    byte[] export(TestCaseFilter filter);
    List<TestCase> createAll(List<TestCase> testCases);
}
