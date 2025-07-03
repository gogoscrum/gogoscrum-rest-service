package com.shimi.gogoscrum.common.util;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class ListOfLongToStringConverter extends ObjectToStringConverter<List<Long>> {
    public ListOfLongToStringConverter() {
        super(new TypeReference<>() {
        });
    }
}
