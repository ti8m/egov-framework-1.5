package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.persistence.base.Parameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;


@Converter
@Slf4j
public class ParametersConverter implements AttributeConverter<Parameters, String> {

    private static final String JSON_PROCESSING_EXCEPTION = "JsonProcessingException";

    // Shared mapper with JavaTimeModule so java.time values in
    // Parameters.params survive a DB round-trip; without it the moment
    // params becomes visible to Jackson (see @JsonProperty on the field)
    // a LocalDateTime entry blows up with InvalidDefinitionException.
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .build();

    @Override
    public String convertToDatabaseColumn(final Parameters parameters) {
        try {
            return OBJECT_MAPPER.writeValueAsString(parameters);
        } catch (final JsonProcessingException e) {
            throw new EGovException(ExceptionCode.DEFAULT, JSON_PROCESSING_EXCEPTION, e);
        }
    }

    @Override
    public Parameters convertToEntityAttribute(final String s) {
        try {
            return OBJECT_MAPPER.readValue(s, Parameters.class);
        } catch (final JsonProcessingException e) {
            throw new EGovException(ExceptionCode.DEFAULT, JSON_PROCESSING_EXCEPTION, e);
        }
    }

}
