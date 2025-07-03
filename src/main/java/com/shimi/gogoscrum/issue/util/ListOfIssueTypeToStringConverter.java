package com.shimi.gogoscrum.issue.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gogoscrum.common.util.ObjectToStringConverter;
import com.shimi.gogoscrum.issue.model.IssueType;

import java.util.List;

public class ListOfIssueTypeToStringConverter extends ObjectToStringConverter<List<IssueType>> {
	public ListOfIssueTypeToStringConverter() {
		super(new TypeReference<>() {
		});
	}
}
