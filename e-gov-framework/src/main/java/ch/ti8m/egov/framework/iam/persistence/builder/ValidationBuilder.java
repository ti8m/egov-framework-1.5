package ch.ti8m.egov.framework.iam.persistence.builder;

import java.util.ArrayList;
import java.util.List;

public final class ValidationBuilder {

    private ValidationBuilder() {

    }

    public static ValidationListBuilder builder() {
        return new ValidationListBuilder();
    }

    public static class ValidationListBuilder {

        private final List<Object> parameters = new ArrayList<>();
        private String operation;

        public ParameterBuilder operation(final String operation) {
            this.operation = operation;
            return new ParameterBuilder();
        }

        public ParameterBuilder greaterThan() {
            this.operation = "GREATER_THAN";
            return new ParameterBuilder();
        }

        public class ParameterBuilder {

            public ParameterBuilder parameter(final Object parameter) {
                parameters.add(parameter);
                return new ParameterBuilder();
            }

            public List<Object> build() {
                final List<Object> validation = new ArrayList<>();
                validation.add(operation);
                validation.addAll(parameters);
                return validation;
            }

        }

    }

}
