package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.framework.persistence.serializer.IdentifiedResponseSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.List;

@Data
@JsonSerialize(using = IdentifiedResponseSerializer.class)
public class IdentifiedResponse {

    private List<Long> id;
    private String propertyName;

    public IdentifiedResponse(final List<Long> id) {
        this.id = id;
        this.propertyName = "id";
    }

    public IdentifiedResponse(final List<Long> id, final String propertyName) {
        this.id = id;
        this.propertyName = propertyName;
    }
}
