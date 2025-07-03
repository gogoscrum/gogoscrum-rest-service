package com.shimi.gogoscrum.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * A generic converter that converts an object of type T to a JSON string and vice versa.
 * This is useful for persisting complex objects in a database as strings.
 *
 * @param <T> the type of the object to be converted
 */
public abstract class ObjectToStringConverter<T> implements AttributeConverter<T, String> {
    private final Logger log = LoggerFactory.getLogger(ObjectToStringConverter.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeReference<T> typeReference;

    protected ObjectToStringConverter(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
    }

    @Override
    public String convertToDatabaseColumn(T data) {
        String value = "";
        try {
            value = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error caught: ", e);
        }
        return value;
    }

    @Override
    public T convertToEntityAttribute(String data) {
        T rootNode = null;

        if (data != null) {
            try {
                rootNode = mapper.readValue(data, typeReference);
            } catch (IOException e) {
                log.error("Error caught: ", e);
            }
        }

        return rootNode;
    }
}
