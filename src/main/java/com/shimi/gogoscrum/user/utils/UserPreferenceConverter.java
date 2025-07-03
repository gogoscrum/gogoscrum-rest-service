package com.shimi.gogoscrum.user.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shimi.gogoscrum.common.util.ObjectToStringConverter;
import com.shimi.gogoscrum.user.model.Preference;

public class UserPreferenceConverter extends ObjectToStringConverter<Preference> {
    public UserPreferenceConverter() {
        super(new TypeReference<>() {
        });
    }
}
