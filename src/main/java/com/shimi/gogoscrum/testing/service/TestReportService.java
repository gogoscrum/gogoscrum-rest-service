package com.shimi.gogoscrum.testing.service;

import com.shimi.gogoscrum.testing.model.TestReport;
import com.shimi.gogoscrum.testing.model.TestReportFilter;
import com.shimi.gsf.core.service.GeneralService;

public interface TestReportService extends GeneralService<TestReport, TestReportFilter> {
    TestReport generateReport(Long testPlanId);
}
