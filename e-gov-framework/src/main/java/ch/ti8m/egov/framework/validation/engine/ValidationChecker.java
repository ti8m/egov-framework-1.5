package ch.ti8m.egov.framework.validation.engine;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ValidationChecker {

    private static final String OPERATOR_SEPARATOR = " ";
    private static final String NOT = "NOT";
    private static final String AND = "AND";
    private static final String OR = "OR";

    private final ExpressionEvaluationComponent expressionEvaluationComponent;

    private final ObjectValueReaderComponent objectValueReaderComponent;

    @Autowired
    public ValidationChecker(final ExpressionEvaluationComponent expressionEvaluationComponent, final ObjectValueReaderComponent objectValueReaderComponent) {
        this.expressionEvaluationComponent = expressionEvaluationComponent;
        this.objectValueReaderComponent = objectValueReaderComponent;
    }

    public boolean isValid(
            final Object validationObject,
            final Object businessObject
    ) {

        final ValidationMethodMapper validationMethodMapper = new ValidationMethodMapper() {
            @Override
            public ModifiableEntity getAggregate() {
                return null;
            }
        };
        if (validationObject == null) {
            return true;
        }
        if (validationObject instanceof List && ((List) validationObject).isEmpty()) {
            return true;
        }
        return (boolean) isValid(validationObject, businessObject, validationMethodMapper, new Integer[]{}, new String[]{});
    }

    public Object isValid(
            final Object validationObject,
            final Object businessObject,
            final ValidationMethodMapper validationMethodMapper,
            final Integer[] indexes,
            final String[] keys
    ) {
        try {
            if (validationObject instanceof List) {
                final List<Object> validationParameters = (List<Object>) validationObject;
                final List<Object> parameters = new ArrayList<>();
                final String[] operatorArray = ((String) validationParameters.get(0)).split(OPERATOR_SEPARATOR);
                final boolean invertResult;
                final String operator;
                if (operatorArray[0].equals(NOT)) {
                    invertResult = true;
                    operator = operatorArray[1];
                } else {
                    invertResult = false;
                    operator = operatorArray[0];
                }
                switch (operator) {
                    case AND:
                        for (final Object validationParameter : validationParameters.subList(1, validationParameters.size())) {
                            if (!(boolean) isValid(validationParameter, businessObject, validationMethodMapper, indexes, keys)) {
                                return invertResult;
                            }
                        }
                        return !invertResult;
                    case OR:
                        for (final Object validationParameter : validationParameters.subList(1, validationParameters.size())) {
                            if ((boolean) isValid(validationParameter, businessObject, validationMethodMapper, indexes, keys)) {
                                return !invertResult;
                            }
                        }
                        return invertResult;
                    default:
                        parameters.addAll(validationParameters.subList(1, validationParameters.size())
                                .stream()
                                .map(parameter -> isValid(parameter, businessObject, validationMethodMapper, indexes, keys))
                                .collect(Collectors.toList()));
                        return expressionEvaluationComponent.evaluateExpression(operatorArray, parameters,
                                validationMethodMapper);
                }

            } else {
                return objectValueReaderComponent.getValue(validationObject, businessObject, validationMethodMapper,
                        indexes, keys);
            }
        } catch (final Exception e) {
            return false;
        }
    }
}
