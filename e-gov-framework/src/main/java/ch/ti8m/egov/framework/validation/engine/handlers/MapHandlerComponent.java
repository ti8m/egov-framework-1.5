package ch.ti8m.egov.framework.validation.engine.handlers;

import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;
import ch.ti8m.egov.framework.validation.engine.ArrayExpander;
import ch.ti8m.egov.framework.validation.engine.ValidationEngine;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import ch.ti8m.egov.framework.validation.engine.ValidationTriple;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MapHandlerComponent implements RecursionHandler {

    @Override
    public ValidationTriple handle(
            final ValidationEntity validationEntity,
            final RuleSet ruleSet,
            final Object businessObject,
            final Object originalSubEntity,
            final ValidationMethodMapper validationMethodMapper,
            final Integer[] indexes,
            final String[] keys,
            final String path,
            final OctFunction<ValidationEntity, RuleSet, Object, Object, ValidationMethodMapper, Integer[], String[], String, ValidationTriple> recursionFunction) {
        final ValidationEntity validationEntityCopy;
        validationEntityCopy = validationEntity.getCopy();
        validationEntityCopy.setType(ValidationEngine.OBJECT);

        final Map<Object, Object> responseMap = new HashMap<>();
        final Map<String, Object> validationResults = new HashMap<>();
        final ValidationTriple handleMapResponse = new ValidationTriple();
        handleMapResponse.setState(ValidationTriple.ValidationState.VALID);
        final Map<String, Object> subEntity = mapObjectToMap(originalSubEntity);
        subEntity.keySet()
                .forEach(key -> {
                            final ValidationTriple recursionResult = recursionFunction.apply(
                                    validationEntityCopy,
                                    ruleSet,
                                    businessObject,
                                    subEntity.get(key),
                                    validationMethodMapper,
                                    indexes,
                                    ArrayExpander.append(keys, key),
                                    path + "." + key
                            );
                            responseMap.put(key, recursionResult.getResponseObject());
                            validationResults.put(key, recursionResult.getValidationResult());
                            if (recursionResult.getState() != ValidationTriple.ValidationState.VALID) {
                                handleMapResponse.setState(ValidationTriple.ValidationState.INVALID);
                            }
                        }
                );
        handleMapResponse.setResponseObject(responseMap);
        handleMapResponse.setValidationResult(validationResults);
        return handleMapResponse;
    }

    private Map<String, Object> mapObjectToMap(final Object object) {
        final Map<Object, Object> targetMap = (Map<Object, Object>) object;
        return targetMap.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().toString(), Map.Entry::getValue));
    }

}
