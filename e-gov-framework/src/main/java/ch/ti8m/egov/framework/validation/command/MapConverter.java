package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Converter
@Slf4j
public class MapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final String JSON_PROCESSING_EXCEPTION = "JsonProcessingException";

    @Override
    public String convertToDatabaseColumn(final Map<String, Object> stringObjectMap) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(stringObjectMap);
        } catch (final JsonProcessingException e) {
            throw new EGovException(ExceptionCode.DEFAULT, JSON_PROCESSING_EXCEPTION, e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(final String s) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final TypeReference<Map<String, Object>> typeReference = new TypeReference<>() {
            };
            return objectMapper.readValue(s, typeReference);
        } catch (final JsonProcessingException e) {
            throw new EGovException(ExceptionCode.DEFAULT, JSON_PROCESSING_EXCEPTION, e);
        }
    }

}
