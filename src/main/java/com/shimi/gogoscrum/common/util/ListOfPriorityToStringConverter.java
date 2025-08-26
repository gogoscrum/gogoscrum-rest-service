package com.shimi.gogoscrum.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gogoscrum.common.model.Priority;

import java.util.List;

public class ListOfPriorityToStringConverter extends ObjectToStringConverter<List<Priority>> {
    public ListOfPriorityToStringConverter() {
        super(new TypeReference<>() {
        });
    }
}
