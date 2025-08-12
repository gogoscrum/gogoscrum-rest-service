package com.shimi.gogoscrum.testing.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gogoscrum.common.util.ObjectToStringConverter;
import com.shimi.gogoscrum.testing.model.TestStepResult;

import java.util.List;

public class ListOfStepResultToStringConverter extends ObjectToStringConverter<List<TestStepResult>> {
	public ListOfStepResultToStringConverter() {
		super(new TypeReference<>() {
		});
	}
}
