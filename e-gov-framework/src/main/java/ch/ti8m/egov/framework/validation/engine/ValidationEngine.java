package ch.ti8m.egov.framework.validation.engine;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.iam.api.java.RuleSetService;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.engine.handlers.AnyHandlerComponent;
import ch.ti8m.egov.framework.validation.engine.handlers.ListHandlerComponent;
import ch.ti8m.egov.framework.validation.engine.handlers.MapHandlerComponent;
import ch.ti8m.egov.framework.validation.engine.handlers.ObjectHandlerComponent;
import ch.ti8m.egov.framework.validation.engine.handlers.RecursionHandlerComponent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Component
@Slf4j
public class ValidationEngine {

    public static final String OBJECT = "object";
    public static final String LIST = "list";
    public static final String MAP = "map";
    public static final String RECURSION = "recursive:root";

    public static final String ANY = "any";
    private static final String VALIDITY = "validity";
    private static final String RULE = "rule";

    private final ObjectHandlerComponent objectHandlerComponent;
    private final ListHandlerComponent listHandlerComponent;
    private final MapHandlerComponent mapHandlerComponent;
    private final RecursionHandlerComponent recursionHandlerComponent;
    private final AnyHandlerComponent anyHandlerComponent;
    private final TypeCastComponent typeCastComponent;
    private final RuleSetService ruleSetService;
    private ValidationChecker validationChecker; // cannot be final due to compatability issues with framework 1.0

    public RuleSet getApplicableRuleSet(final Command command, final ValidationMethodMapper validationMethodMapper) {
        return ruleSetService.getRuleSet(
                DataHolder.getUserId(),
                command.getAction(),
                validationMethodMapper == null
                        ? null
                        : validationMethodMapper.getAggregate() == null
                        ? null
                        : validationMethodMapper.getAggregate().getAggregateStatus()
        );
    }

    public ValidationTriple validate(
            final RuleSet workingRuleSet,
            final Object value,
            final String action,
            final ValidationMethodMapper validationMethodMapper
    ) {
        if (workingRuleSet == null
                || workingRuleSet.getValidationEntity() == null
                || workingRuleSet.getValidationEntity().entrySet() == null
                || workingRuleSet.getValidationEntity().entrySet().isEmpty()) {
            log.debug("[DEVELOPER MODE] No working ruleset selected for action: " + action);
            return new ValidationTriple(value, Collections.emptyMap(), ValidationTriple.ValidationState.VALID);
        }
        return buildResponse(
                workingRuleSet.getValidationEntity(),
                workingRuleSet,
                value,
                value,
                validationMethodMapper,
                new Integer[0],
                new String[0],
                "root"

        );
    }

    public ValidationTriple buildResponse(
            final ValidationEntity validationEntity,
            final RuleSet ruleSet,
            final Object businessObject,
            final Object subEntity,
            final ValidationMethodMapper validationMethodMapper,
            final Integer[] indexes,
            final String[] keys,
            final String path
    ) {
        if (validationEntity == null || (subEntity == null && validationEntity.getValidation() == null)) {
            return new ValidationTriple(
                    null,
                    Collections.emptyMap(),
                    ValidationTriple.ValidationState.VALID
            );
        }
        final Map<String, Object> validationResults = new HashMap<>();
        validationResults.put(VALIDITY, isValid(validationEntity, businessObject, validationMethodMapper, indexes, keys));
        if (validationEntity.getValidation() != null) {
            validationResults.put(RULE, validationEntity.getValidation());
        }
        final ValidationTriple recursionResult;
        switch (validationEntity.getType()) {
            case OBJECT:
                recursionResult = objectHandlerComponent.handle(
                        validationEntity,
                        ruleSet,
                        businessObject,
                        subEntity,
                        validationMethodMapper,
                        indexes,
                        keys,
                        path,
                        this::buildResponse
                );
                break;
            case LIST:
                recursionResult = listHandlerComponent.handle(
                        validationEntity,
                        ruleSet,
                        businessObject,
                        subEntity,
                        validationMethodMapper,
                        indexes,
                        keys,
                        path,
                        this::buildResponse
                );
                break;
            case MAP:
                recursionResult = mapHandlerComponent.handle(
                        validationEntity,
                        ruleSet,
                        businessObject,
                        subEntity,
                        validationMethodMapper,
                        indexes,
                        keys,
                        path,
                        this::buildResponse
                );
                break;
            case ANY:
                recursionResult = anyHandlerComponent.handle(
                        validationEntity,
                        ruleSet,
                        businessObject,
                        subEntity,
                        validationMethodMapper,
                        indexes,
                        keys,
                        path,
                        this::buildResponse
                );
                break;
            default:
                if (validationEntity.getType().startsWith(RECURSION)) {
                    recursionResult = recursionHandlerComponent.handle(
                            validationEntity,
                            ruleSet,
                            businessObject,
                            subEntity,
                            validationMethodMapper,
                            indexes,
                            keys,
                            path,
                            this::buildResponse
                    );
                    break;
                }

                recursionResult = typeCastComponent.castPrimitive(validationEntity, subEntity);
                if (!recursionResult.getState().equals(ValidationTriple.ValidationState.VALID)) {
                    validationResults.put(VALIDITY, ValidationTriple.ValidationState.INCORRECT_FORMAT);
                }
        }
        validationResults.putAll(recursionResult.getValidationResult());
        return new ValidationTriple(
                recursionResult.getResponseObject(),
                validationResults,
                recursionResult.getState() == ValidationTriple.ValidationState.VALID
                        && validationResults.get(VALIDITY) == ValidationTriple.ValidationState.VALID
                        ? ValidationTriple.ValidationState.VALID
                        : ValidationTriple.ValidationState.INVALID
        );
    }

    private ValidationTriple.ValidationState isValid(
            final ValidationEntity validationEntity,
            final Object businessObject,
            final ValidationMethodMapper validationMethodMapper,
            final Integer[] indexes,
            final String[] keys
    ) {
        return validationEntity.getValidation() == null
                || validationEntity.getValidation().isEmpty()
                || (boolean) validationChecker.isValid(validationEntity.getValidation(), businessObject, validationMethodMapper, indexes, keys)
                ? ValidationTriple.ValidationState.VALID
                : ValidationTriple.ValidationState.INVALID;
    }

    public void setValidationChecker(ValidationChecker validationChecker) {
        if (this.validationChecker == null) {
            this.validationChecker = validationChecker;
        }
    }
}
