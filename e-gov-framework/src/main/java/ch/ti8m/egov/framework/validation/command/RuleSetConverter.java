package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class RuleSetConverter implements AttributeConverter<Object, String> {

    private static final String JSON_PROCESSING_EXCEPTION = "JsonProcessingException";

    @Override
    public String convertToDatabaseColumn(final Object object) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException e) {
            throw new EGovException(ExceptionCode.DEFAULT, JSON_PROCESSING_EXCEPTION, e);
        }
    }

    @Override
    public RuleSet convertToEntityAttribute(final String s) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            return objectMapper.readValue(s, RuleSet.class);
        } catch (final JsonProcessingException e) {
            throw new EGovException(ExceptionCode.DEFAULT, JSON_PROCESSING_EXCEPTION, e);
        }
    }

}
