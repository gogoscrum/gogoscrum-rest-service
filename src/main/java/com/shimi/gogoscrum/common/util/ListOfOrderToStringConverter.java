package com.shimi.gogoscrum.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gsf.core.model.Filter;

import java.util.List;

public class ListOfOrderToStringConverter extends ObjectToStringConverter<List<Filter.Order>> {
	public ListOfOrderToStringConverter() {
		super(new TypeReference<>() {
		});
	}
}
