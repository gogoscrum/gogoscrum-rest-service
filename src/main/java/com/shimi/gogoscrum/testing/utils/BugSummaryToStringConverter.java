package com.shimi.gogoscrum.testing.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gogoscrum.common.util.ObjectToStringConverter;
import com.shimi.gogoscrum.testing.model.TestReport;

public class BugSummaryToStringConverter extends ObjectToStringConverter<TestReport.BugSummary> {
	public BugSummaryToStringConverter() {
		super(new TypeReference<>() {
		});
	}
}
