package ch.ti8m.egov.framework.validation.engine.handlers;

import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import ch.ti8m.egov.framework.validation.engine.ValidationTriple;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

@Component
public class RecursionHandlerComponent implements RecursionHandler {

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
        final Queue<String> fieldPath = new LinkedList<>(Arrays.asList(validationEntity.getType().split("\\.")));
        fieldPath.remove();
        final ValidationEntity recursedValidationEntity = getValidationEntity(ruleSet.getValidationEntity(), fieldPath);
        return recursionFunction.apply(
                recursedValidationEntity,
                ruleSet,
                businessObject,
                subEntity,
                validationMethodMapper,
                indexes,
                keys,
                path
        );
    }

    private ValidationEntity getValidationEntity(
            final ValidationEntity validationEntity,
            final Queue<String> fieldPath
    ) {
        if (fieldPath.isEmpty()) {
            return validationEntity;
        }
        final String subField = fieldPath.remove();
        return getValidationEntity(
                validationEntity.getFields().get(subField),
                fieldPath
        );
    }

}
