package com.shimi.gogoscrum.sprint.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gogoscrum.common.util.ObjectToStringConverter;
import com.shimi.gogoscrum.issue.dto.IssueCountDto;
import jakarta.persistence.Converter;

import java.util.List;
import java.util.Map;

@Converter
public class IssueCountsToStringJpaConverter extends ObjectToStringConverter<Map<String, List<IssueCountDto>>> {
    public IssueCountsToStringJpaConverter() {
        super(new TypeReference<>() {
        });
    }
}
