package ch.ti8m.egov.framework.iam.persistence.model.ruleset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationEntity implements Serializable {

    private Map<String, ValidationEntity> fields = new HashMap<>();
    private String type;
    private String subtype;
    private List<Object> validation;

    public ValidationEntity get(final String key) {
        return fields.get(key);
    }

    public Set<String> keySet() {
        return fields.keySet();
    }

    public Collection<ValidationEntity> values() {
        return fields.values();
    }

    public Set<Map.Entry<String, ValidationEntity>> entrySet() {
        return fields.entrySet();
    }

    @JsonIgnore
    public ValidationEntity getCopy() {
        return new ValidationEntity(
                new HashMap<>(fields),
                type,
                subtype,
                validation == null ? null : new ArrayList<>(validation)
        );
    }

}
