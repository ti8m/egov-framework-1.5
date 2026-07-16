package ch.ti8m.egov.framework.validation.engine.handlers;

import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;
import ch.ti8m.egov.framework.validation.engine.FieldValueExtractorComponent;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import ch.ti8m.egov.framework.validation.engine.ValidationTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ObjectHandlerComponent implements RecursionHandler {

    private final FieldValueExtractorComponent fieldValueExtractorComponent;

    @Autowired
    public ObjectHandlerComponent(FieldValueExtractorComponent fieldValueExtractorComponent) {
        this.fieldValueExtractorComponent = fieldValueExtractorComponent;
    }

    @Override
    public ValidationTriple handle(
            final ValidationEntity validationEntity,
            final RuleSet ruleSet,
            final Object businessObject,
            final Object subEntity,
            final ValidationMethodMapper validationMethodMapper,
            final Integer[] indexes,
            final String[] keys,
            final String path,
            final OctFunction<ValidationEntity, RuleSet, Object, Object, ValidationMethodMapper, Integer[], String[], String, ValidationTriple> recursionFunction
    ) {
        final Map<String, Object> resultEntity = new HashMap<>();
        final Map<String, Object> validationResults = new HashMap<>();
        final ValidationTriple handleObjectResponse = new ValidationTriple();
        handleObjectResponse.setState(ValidationTriple.ValidationState.VALID);
        validationEntity.keySet().forEach(key -> {
                    final ValidationTriple result = recursionFunction.apply(
                            validationEntity.get(key),
                            ruleSet,
                            businessObject,
                            fieldValueExtractorComponent.getDeclaredField(key, subEntity, path + "." + key),
                            validationMethodMapper,
                            indexes,
                            keys,
                            path + "." + key
                    );
                    resultEntity.put(key, result.getResponseObject());
                    validationResults.put(key, result.getValidationResult());
                    if (result.getState() != ValidationTriple.ValidationState.VALID) {
                        handleObjectResponse.setState(ValidationTriple.ValidationState.INVALID);
                    }
                }
        );
        handleObjectResponse.setResponseObject(resultEntity);
        handleObjectResponse.setValidationResult(validationResults);
        return handleObjectResponse;
    }

}
