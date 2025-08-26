package com.shimi.gogoscrum.testing.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gogoscrum.common.util.ObjectToStringConverter;
import com.shimi.gogoscrum.testing.model.TestReport;

public class CaseSummaryToStringConverter extends ObjectToStringConverter<TestReport.CaseSummary> {
	public CaseSummaryToStringConverter() {
		super(new TypeReference<>() {
		});
	}
}
