package ch.ti8m.egov.mdm.persistence.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Converter
@Slf4j
public class ObjectConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @SneakyThrows
    public String convertToDatabaseColumn(final Map<String, Object> content) {
        return objectMapper.writeValueAsString(content);
    }

    @Override
    @SneakyThrows
    public Map<String, Object> convertToEntityAttribute(final String contentJson) {
        if (contentJson == null || contentJson.isEmpty()) {
            return new HashMap<>();
        }
        return objectMapper.readValue(contentJson, new TypeReference<>() {
        });
    }

}
