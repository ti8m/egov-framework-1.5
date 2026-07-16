package ch.ti8m.egov.framework.iam.persistence.builder;


import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;

import java.util.List;
import java.util.Map;

public final class ValidationEntityBuilder {

    private ValidationEntityBuilder() {

    }

    public static ValidationEntityTypeBuilder builder() {
        return new ValidationEntityTypeBuilder();
    }

    public static class ValidationEntityTypeBuilder {

        private String type;

        public ValidationEntitySubTypeBuilder type(final String type) {
            this.type = type;
            return new ValidationEntitySubTypeBuilder();
        }

        public ValidationEntitySubTypeBuilder string() {
            this.type = "string";
            return new ValidationEntitySubTypeBuilder();
        }

        public class ValidationEntitySubTypeBuilder {

            private String subtype;

            public ValidationEntityValidationBuilder subtype(final String subtype) {
                this.subtype = subtype;
                return new ValidationEntityValidationBuilder();
            }

            public class ValidationEntityValidationBuilder {

                private List<Object> validation;

                public ValidationEntityFields validation(final List<Object> validation) {
                    this.validation = validation;
                    return new ValidationEntityFields();
                }

                public class ValidationEntityFields {

                    private Map<String, ValidationEntity> fields;

                    public ValidationEntityCompiler fields(final Map<String, ValidationEntity> fields) {
                        this.fields = fields;
                        return new ValidationEntityCompiler();
                    }

                    public class ValidationEntityCompiler {
                        public ValidationEntity build() {
                            return new ValidationEntity(
                                    fields,
                                    type,
                                    subtype,
                                    validation
                            );
                        }

                    }
                }

            }

        }

    }

}