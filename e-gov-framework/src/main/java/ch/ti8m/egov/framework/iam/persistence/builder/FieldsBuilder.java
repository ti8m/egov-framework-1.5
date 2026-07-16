package ch.ti8m.egov.framework.iam.persistence.builder;


import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;

import java.util.HashMap;
import java.util.Map;

public final class FieldsBuilder {

    private FieldsBuilder() {
    }

    public static ValidationEntityFieldBuilder builder() {
        return new ValidationEntityFieldBuilder();
    }

    public static class ValidationEntityFieldBuilder {

        private final Map<String, ValidationEntity> fields = new HashMap<>();

        public ValidationEntityFieldBuilder field(final String fieldName, final ValidationEntity validationEntity) {
            fields.put(fieldName, validationEntity);
            return this;
        }

        public Map<String, ValidationEntity> build() {
            return fields;
        }

    }

}
