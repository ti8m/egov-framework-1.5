package ch.ti8m.egov.framework.validation.command.globalcommand;

import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.DomainCommandFactory;
import ch.ti8m.egov.framework.validation.command.action.BaseAction;
import ch.ti8m.egov.framework.validation.command.globalcommand.execution.RemoteCommandExecutor;
import ch.ti8m.egov.framework.validation.command.globalcommand.execution.RemoteCommandHandlerProxy;
import ch.ti8m.egov.framework.validation.command.proxy.CommandHandlerProxy;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalCommandRunner extends DomainCommandFactory {
    private final ActionRegistrationService actionRegistrationService;
    private final ObjectMapper objectMapper;

    @Override
    protected void setExecutionDetails(final Command command) {
        ActionConfiguration actionConfiguration = handleNullActionConfiguration(
                command, actionRegistrationService.getActionConfiguration(command.getAction()));
        setExecutor(command, actionConfiguration.getExecutorClass());
        command.setExecutionPlan(actionConfiguration.getExecutionPlan());
    }

    public CommandHandlerProxy getCommandHandlerProxy(final Command command) {
        final ActionConfiguration actionConfiguration = handleNullActionConfiguration(
                command, actionRegistrationService.getActionConfiguration(command.getAction()));
        return applicationContext.getBean(actionConfiguration.getCommandHandlerProxyClass());
    }

    public Object run(final Command command) {
        final CommandHandlerProxy commandHandlerProxy = getCommandHandlerProxy(command);
        final ActionConfiguration actionConfiguration = handleNullActionConfiguration(
                command, actionRegistrationService.getActionConfiguration(command.getAction()));
        command.setDomain(actionConfiguration.getDomain());
        return commandHandlerProxy.handleCommand(command);
    }

    @Deprecated
    public Object run(
            final BaseAction action,
            final Object value,
            final String userId,
            final Parameters parameters
    ) {
        final Command command = getCommand(
                action,
                value,
                userId,
                parameters
        );
        return run(command);
    }

    public <T> T run(
            final BaseAction action,
            final Object value,
            final Parameters parameters,
            final TypeReference<T> typeReference
    ) {
        final Object plainResult = run(action, value, parameters);
        return objectMapper.convertValue(plainResult, typeReference);
    }

    public Object run(
            final BaseAction action,
            final Object value,
            final Parameters parameters
    ) {
        final Command command = getCommand(
                action,
                value,
                parameters
        );
        return run(command);
    }

    public Object run(
            final String action,
            final String aggregateName,
            final Object value,
            final Parameters parameters
    ) {
        final Command command = getCommand(
                action,
                aggregateName,
                value,
                parameters
        );
        return run(command);
    }

    private ActionConfiguration handleNullActionConfiguration(final Command command, @Nullable ActionConfiguration actionConfiguration) {
        if (actionConfiguration == null) {
            actionRegistrationService.registerAction(ActionConfiguration.local(
                    command.getAction(),
                    RemoteCommandExecutor.class,
                    Command.ExecutionPlan.DONT_VALIDATE,
                    RemoteCommandHandlerProxy.class
            ));
            return actionRegistrationService.getActionConfiguration(command.getAction());
        }
        return actionConfiguration;
    }

}
