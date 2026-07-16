package ch.ti8m.egov.framework.persistence.serializer;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.IdentifiedResponse;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;


public class IdentifiedResponseSerializer extends JsonSerializer<IdentifiedResponse> {

    @Override
    public void serialize(final IdentifiedResponse identifiedResponse, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if (identifiedResponse.getId().size() == 1) {
            jsonGenerator.writeNumberField(identifiedResponse.getPropertyName(), identifiedResponse.getId().get(0));
        } else {
            jsonGenerator.writeFieldName(identifiedResponse.getPropertyName());
            jsonGenerator.writeStartArray();
            identifiedResponse.getId().forEach(id -> {
                try {
                    jsonGenerator.writeNumber(id);
                } catch (final IOException e) {
                    throw new EGovException(e);
                }
            });
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();
    }
}
