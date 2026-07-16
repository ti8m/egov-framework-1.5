package ch.ti8m.egov.framework.validation.engine.handlers;


import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;
import ch.ti8m.egov.framework.validation.engine.ArrayExpander;
import ch.ti8m.egov.framework.validation.engine.ValidationEngine;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import ch.ti8m.egov.framework.validation.engine.ValidationTriple;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ListHandlerComponent implements RecursionHandler {

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
        final ValidationEntity validationEntityCopy;
        validationEntityCopy = validationEntity.getCopy();
        final String subtype = validationEntity.getSubtype() == null
                ? ValidationEngine.OBJECT
                : validationEntity.getSubtype();
        validationEntityCopy.setType(subtype);
        final List<Object> result = new ArrayList<>();
        final Map<String, Object> validationResult = new HashMap<>();
        final ValidationTriple handleListResponse = new ValidationTriple();
        handleListResponse.setState(ValidationTriple.ValidationState.VALID);
        for (int i = 0; i < ((List<Object>) subEntity).size(); i++) {
            final ValidationTriple recursionResult = recursionFunction.apply(
                    validationEntityCopy,
                    ruleSet,
                    businessObject,
                    ((List<Object>) subEntity).get(i),
                    validationMethodMapper,
                    ArrayExpander.append(indexes, i),
                    keys,
                    path + "[" + i + "]"
            );
            result.add(recursionResult.getResponseObject());
            validationResult.put(String.valueOf(i), recursionResult.getValidationResult());
            if (recursionResult.getState() != ValidationTriple.ValidationState.VALID) {
                handleListResponse.setState(ValidationTriple.ValidationState.INVALID);
            }
        }
        handleListResponse.setResponseObject(result);
        handleListResponse.setValidationResult(validationResult);
        return handleListResponse;
    }

}
