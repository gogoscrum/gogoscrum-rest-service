package com.shimi.gogoscrum.issue.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gogoscrum.common.util.ObjectToStringConverter;
import com.shimi.gogoscrum.issue.model.IssuePriority;

import java.util.List;

public class ListOfIssuePriorityToStringConverter extends ObjectToStringConverter<List<IssuePriority>> {
    public ListOfIssuePriorityToStringConverter() {
        super(new TypeReference<>() {
        });
    }
}
