package ch.ti8m.egov.framework.validation.engine.handlers;


import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import ch.ti8m.egov.framework.validation.engine.ValidationTriple;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class AnyHandlerComponent implements RecursionHandler {

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
        return new ValidationTriple(
                subEntity,
                Collections.emptyMap(),
                ValidationTriple.ValidationState.VALID
        );
    }
}
