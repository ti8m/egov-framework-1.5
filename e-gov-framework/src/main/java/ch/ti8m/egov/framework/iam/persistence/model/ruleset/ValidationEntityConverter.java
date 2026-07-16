package ch.ti8m.egov.framework.iam.persistence.model.ruleset;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class ValidationEntityConverter implements AttributeConverter<ValidationEntity, String> {

    @Override
    public String convertToDatabaseColumn(final ValidationEntity validationEntity) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(validationEntity);
        } catch (final JsonProcessingException e) {
            throw new EGovException(ExceptionCode.ERROR_PROCESSING_VALIDATION_ENTITY, "unable to convert to database column");
        }
    }

    @Override
    public ValidationEntity convertToEntityAttribute(final String validationEntityString) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(validationEntityString, ValidationEntity.class);
        } catch (final JsonProcessingException e) {
            throw new EGovException(ExceptionCode.ERROR_PROCESSING_VALIDATION_ENTITY, "unable to convert to entity attribute");
        }
    }

}
