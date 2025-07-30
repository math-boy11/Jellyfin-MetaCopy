package com.mathboy11;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
