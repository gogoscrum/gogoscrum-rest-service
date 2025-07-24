package com.shimi.gogoscrum.testing.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gogoscrum.common.util.ObjectToStringConverter;
import com.shimi.gogoscrum.testing.model.TestStep;

import java.util.List;

public class ListOfStepToStringConverter extends ObjectToStringConverter<List<TestStep>> {
	public ListOfStepToStringConverter() {
		super(new TypeReference<>() {
		});
	}
}
