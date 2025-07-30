package com.mathboy11;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class ConfigLoader {
    public static Properties loadConfig(String configFilePath) {
        Properties properties = new Properties();

        try {
            InputStream stream;

            if (configFilePath != null && !configFilePath.isBlank()) {
                // Load the config from the user-specified path
                stream = new FileInputStream(configFilePath);
            } else {
                // Load the config from the classpath
                stream = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties");

                if (stream == null) {
                    throw new RuntimeException("Config file not found");
                }
            }

            try (stream) {
                properties.load(stream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config");
        }

        return properties;
    }

    public static HashMap<String, ArrayList<String>> loadFieldMapping() {
        try (InputStream stream = ConfigLoader.class.getClassLoader().getResourceAsStream("field-mapping.json")) {
            if (stream == null) {
                throw new RuntimeException("Field mapping file not found");
            }

            return JacksonUtil.getObjectMapper().readValue(stream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load field mapping");
        }
    }
}
