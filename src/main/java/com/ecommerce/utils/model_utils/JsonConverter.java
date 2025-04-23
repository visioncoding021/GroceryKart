package com.ecommerce.utils.model_utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Converter(autoApply = true) // Auto-applies this converter to all JSON fields
public class JsonConverter implements AttributeConverter<Map<String, String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Convert Map to JSON String before storing in MySQL
    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Map to JSON String", e);
        }
    }

    // Convert JSON String from MySQL to Map
    @Override
        public Map<String, String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(dbData, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON String to Map", e);
        }
    }
}
