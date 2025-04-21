package com.ecommerce.utils.service_utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProductUtils {
    public static Map<String, String> parseMetadata(String metadataJson) {
        Map<String, String> metadataMap = new HashMap<>();

        // Remove curly braces and split by comma
        String cleanedJson = metadataJson.replaceAll("[{}\"]", "").trim();
        String[] keyValuePairs = cleanedJson.split(",");

        for (String pair : keyValuePairs) {
            // Split each pair by colon to separate key and value
            String[] keyValue = pair.split(":");

            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                // Put key-value pair in map
                metadataMap.put(key, value);
            }
        }

        return metadataMap;
    }
}
