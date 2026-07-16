package ch.ti8m.egov.framework.validation.command.handler;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.exceptionhandling.model.PaginatedResult;
import ch.ti8m.egov.framework.iam.api.java.RoleService;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.CommandRepository;
import ch.ti8m.egov.framework.validation.command.subscription.SubscriptionService;
import ch.ti8m.egov.framework.validation.engine.ValidationEngine;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import ch.ti8m.egov.framework.validation.engine.ValidationTriple;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Data
@Component
public abstract class CommandHandlerBase<T> implements CommandHandler {
    public static final String VALIDATION_STATE = "validationState";
    public static final String VALIDATION_RESULT = "validationResult";

    @Autowired
    private ValidationEngine validationEngine;
    @Autowired
    private CommandRepository commandRepository;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PrimaryRepositoryResolverComponent primaryRepositoryResolverComponent;
    private ValidationMethodMapper validationMethodMapper;

    @Override
    public Object handleCommand(final Command command) {
        try {
            // We need the commandId for the audit log. We save it first to get the database-ID
            command.setStatus(Command.Status.INITIALIZED);
            final Long currentCommandId = DataHolder.getCurrentCommandId();
            if (currentCommandId != -1) {
                command.setParentid(currentCommandId);
            }
            command.setAbsoluteRequestPath(DataHolder.getAbsoluteRequestPath() == null ? "Command not invoked via REST API" : DataHolder.getAbsoluteRequestPath());
            commandRepository.saveWithTx(command);
            DataHolder.pushCommandId(command.getId());
            validationMethodMapper = provideValidationMethodMapper();
            // reset aggregate
            DataHolder.setAggregate(null);
            if (command.getParameters().getAggregateId() != null) {
                loadAggregate(command);
            }
            command.setSelectedRuleSet(validationEngine.getApplicableRuleSet(command, validationMethodMapper));
            command.setStatus(Command.Status.RULESET_SELECTED);
            updateCommand(command);

            final Object response = orchestrateCommandHandling(command);

            command.setStatus(Command.Status.COMPLETED);
            updateCommand(command);

            subscriptionService.runSubscriptions(command);
            return response;
        } catch (final RuntimeException e) {
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            command.setError(ExceptionUtils.getStackTrace(e));
            final UUID exceptionId = UUID.randomUUID();
            command.setExceptionId(exceptionId.toString());
            DataHolder.setExceptionId(exceptionId);
            if (command.getId() == null) {
                saveCommand(command);
            } else {
                updateCommand(command);
            }
            DataHolder.setDebugPermission(userHasDebugPermission());
            if (e instanceof final EGovException eGovException && command.getId() != null) {
                eGovException.addAdditionalInfo("commandId", command.getId().toString());
            } else {
                CommandHandlerBase.log.debug("Error while handling command id: %s".formatted(command.getId()));
            }
            throw e;
        } finally {
            DataHolder.popCommandId();
        }
    }

    private Boolean userHasDebugPermission() {
        return roleService.getRolesByUserId(DataHolder.getUserId()).contains("RO_Debugging");
    }

    private Object orchestrateCommandHandling(final Command command) {
        return switch (command.getExecutionPlan()) {
            case VALIDATE_BEFORE_EXECUTING_ANYWAY -> validateBeforeExecutingAnyway(command);
            case VALIDATE_BEFORE_EXECUTION -> validateBeforeExecution(command);
            case VALIDATE_AFTER_EXECUTION -> validateAfterExecution(command);
            case DONT_VALIDATE -> dontValidate(command);
            case DONT_EXECUTE -> dontExecute(command);
        };
    }

    private Object validateBeforeExecutingAnyway(final Command command) {
        CommandHandlerBase.log.debug("---VALIDATING " + command.getAction() + "---");
        final ValidationTriple validationResult = validate(
                command.getSelectedRuleSet(),
                command.getCommandValue(),
                command.getAction(),
                command.getValidateValueAsMap()
        );
        command.setCommandValue(validationResult.getResponseObject());
        command.setValidationResult(validationResult);
        CommandHandlerBase.log.debug("---EXECUTING " + command.getAction() + "---");

        command.setStatus(Command.Status.VALIDATED);
        updateCommand(command);
        return command.execute();
    }

    private Object dontValidate(final Command command) {
        CommandHandlerBase.log.debug("---EXECUTING " + command.getAction() + "---");
        command.setCommandValue(command.execute());
        return command.getCommandValue();
    }

    private Map<String, Object> dontExecute(final Command command) {
        CommandHandlerBase.log.debug("---VALIDATING " + command.getAction() + "---");
        final ValidationTriple validationResult = validate(
                command.getSelectedRuleSet(),
                command.getCommandValue(),
                command.getAction(),
                command.getValidateValueAsMap());
        command.setValidationResult(validationResult);

        command.setStatus(Command.Status.VALIDATED);
        updateCommand(command);
        return Map.of(CommandHandlerBase.VALIDATION_STATE, command.getValidationResult().getState(),
                CommandHandlerBase.VALIDATION_RESULT, command.getValidationResult().getValidationResult());
    }

    private Object validateBeforeExecution(final Command command) {
        final ValidationTriple validationResult = validate(
                command.getSelectedRuleSet(),
                command.getCommandValue(),
                command.getAction(),
                command.getValidateValueAsMap());
        if (validationResult.getState() == ValidationTriple.ValidationState.VALID) {
            command.setCommandValue(validationResult.getResponseObject());
            command.setValidationResult(validationResult);

            command.setStatus(Command.Status.VALIDATED);
            updateCommand(command);

            return command.execute();
        } else {
            command.setStatus(Command.Status.HARD_VALIDATION_FAILED);
            updateCommand(command);
            throw new EGovException(ExceptionCode.INVALID_DATA, "Data Validation failed",
                    Map.of(CommandHandlerBase.VALIDATION_STATE, validationResult.getState(),
                            CommandHandlerBase.VALIDATION_RESULT, validationResult.getValidationResult()));
        }
    }


    private Object validateAfterExecution(final Command command) {
        final ValidationTriple validationResult;
        final Object executionResult = command.execute();

        command.setStatus(Command.Status.EXECUTED);
        updateCommand(command);

        validationResult = validate(
                command.getSelectedRuleSet(),
                executionResult,
                command.getAction(),
                command.getValidateValueAsMap());

        command.setStatus(Command.Status.VALIDATED);
        updateCommand(command);

        if (validationResult.getResponseObject() instanceof List) {
            command.setValidationResult(validationResult);
            if (!primaryRepositoryResolverComponent.isPrimaryRepositoryPresent(command.getExecutor()) || command.getParameters().isSkipPagination()) {
                command.setCommandValue(validationResult.getResponseObject());
            } else {
                command.setCommandValue(new PaginatedResult<>(
                        (List<?>) validationResult.getResponseObject(),
                        DataHolder.getCount() == null ? -1 : DataHolder.getCount())
                );
            }
        } else {
            command.setCommandValue(validationResult.getResponseObject());
        }
        return command.getCommandValue();
    }

    private ValidationTriple validate(
            final RuleSet workingRuleSet,
            final Object value,
            final String action,
            final Boolean validateValueAsMap
    ) {
        if (value instanceof final List<?> nonEmptyList && !nonEmptyList.isEmpty()) {
            final ValidationTriple validationTriple = validationEngine.validate(
                    workingRuleSet,
                    nonEmptyList,
                    action,
                    validationMethodMapper
            );
            if (validateValueAsMap == null || !validateValueAsMap) {
                validationTriple.setResponseObject(value);
            }
            return validationTriple;
        } else if (value == null || value instanceof List<?>) {
            // value is null or empty list
            return validationEngine.validate(
                    workingRuleSet,
                    value,
                    action,
                    validationMethodMapper
            );
        } else {
            final ValidationTriple validationTriple = validationEngine.validate(
                    workingRuleSet,
                    value,
                    action,
                    validationMethodMapper
            );
            if (validateValueAsMap == null || !validateValueAsMap) {
                validationTriple.setResponseObject(value);
            }
            return validationTriple;
        }
    }

    private JavaType getTypeReference(final List<?> nonEmptyList) {
        return TypeFactory.defaultInstance().constructCollectionType(List.class, nonEmptyList.get(0).getClass());
    }

    protected abstract void loadAggregate(Command command);

    protected abstract ValidationMethodMapper provideValidationMethodMapper();

    public T getAggregate() {
        return DataHolder.getAggregate();
    }

    public void setAggregate(final T aggregate) {
        DataHolder.setAggregate(aggregate);
    }

    private void updateCommand(final Command command) {
        commandRepository.deactivatePermissions();
        commandRepository.updateWithTx(command);
        commandRepository.activatePermissions();
    }

    private void saveCommand(final Command command) {
        commandRepository.saveWithTx(command);
    }
}
