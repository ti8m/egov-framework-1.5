package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.validation.command.mixin.MixInByteArrayOutputStream;
import ch.ti8m.egov.framework.validation.command.mixin.MixInStreamingResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Proxy;

@Converter
@Slf4j
public class ObjectConverter implements AttributeConverter<Object, String> {

    private Integer valueLength;

    @Override
    public String convertToDatabaseColumn(final Object object) {
        if (valueLength == null) {
            valueLength = Integer.parseInt(SpringContext.getProperty("egov.persistence.database.command.value-length", "8000"));
        }
        if (object instanceof Proxy) {
            return object.toString();
        }
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.addMixIn(ByteArrayOutputStream.class, MixInByteArrayOutputStream.class);
            objectMapper.addMixIn(StreamingResponseBody.class, MixInStreamingResponseBody.class);
            objectMapper.registerModule(new IgnoreMultipartFileModule());

            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.registerModule(new JavaTimeModule());
            final String result = objectMapper.writeValueAsString(object);
            if (result == null) {
                return result;
            }
            if (result.length() < valueLength) {
                return result;
            }
            return result.substring(0, valueLength);
        } catch (final JsonProcessingException e) {
            ObjectConverter.log.error("Could not convert to Json. Instance of {}", object.getClass(), e);
            return "{}";
        }
    }

    @Override
    public Object convertToEntityAttribute(final String s) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(s, Object.class);
        } catch (final JsonProcessingException e) {
            ObjectConverter.log.error("Could not deserialize from Json. String {}", s, e);
            return null;
        }
    }

}