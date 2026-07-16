package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.deployconfig.VersionProvider;
import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.action.BaseAction;
import ch.ti8m.egov.framework.validation.command.executor.BaseExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public abstract class DomainCommandFactory implements ApplicationContextAware {

    protected static ApplicationContext applicationContext;

    @Autowired
    private VersionProvider versionProvider;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        DomainCommandFactory.applicationContext = applicationContext;
    }

    public Command getCommand(
            final BaseAction action,
            final Object value,
            final String userId,
            final Parameters parameters
    ) {
        parameters.setUserId(userId);
        final Command command = buildCommand(
                action,
                value,
                userId,
                parameters
        );
        setExecutionDetails(command);
        command.setAggregateId(parameters.getAggregateId());
        return command;
    }

    public Command getCommand(
            final BaseAction action,
            final Object value,
            final Parameters parameters
    ) {
        parameters.setUserId(DataHolder.getUserId());
        final Command command = buildCommand(
                action,
                value,
                DataHolder.getUserId(),
                parameters
        );
        setExecutionDetails(command);
        command.setAggregateId(parameters.getAggregateId());
        return command;
    }

    public Command getCommand(
            final BaseAction action,
            final Parameters parameters
    ) {
        parameters.setUserId(DataHolder.getUserId());
        final Command command = buildCommand(
                action,
                null,
                DataHolder.getUserId(),
                parameters
        );
        setExecutionDetails(command);
        command.setAggregateId(parameters.getAggregateId());
        return command;
    }

    public Command getCommand(
            final BaseAction action,
            final Long aggregateId
    ) {
        final Parameters parameters = Parameters.builder()
                .userId(DataHolder.getUserId())
                .aggregateId(aggregateId)
                .build();
        final Command command = buildCommand(
                action,
                null,
                DataHolder.getUserId(),
                parameters
        );
        setExecutionDetails(command);
        command.setAggregateId(parameters.getAggregateId());
        return command;
    }

    public Command getCommand(
            final BaseAction action
    ) {
        final Parameters parameters = Parameters.builder()
                .userId(DataHolder.getUserId())
                .build();
        final Command command = buildCommand(
                action,
                null,
                DataHolder.getUserId(),
                parameters
        );
        setExecutionDetails(command);
        command.setAggregateId(parameters.getAggregateId());
        return command;
    }

    public Command getCommand(
            final BaseAction action,
            final Object value
    ) {
        final Parameters parameters = Parameters.builder()
                .userId(DataHolder.getUserId())
                .build();
        final Command command = buildCommand(
                action,
                value,
                DataHolder.getUserId(),
                parameters
        );
        setExecutionDetails(command);
        command.setAggregateId(parameters.getAggregateId());
        return command;
    }

    public Command getCommand(
            final BaseAction action,
            final Long aggregateId,
            final Object value
    ) {
        final Parameters parameters = Parameters.builder()
                .userId(DataHolder.getUserId())
                .aggregateId(aggregateId)
                .build();
        final Command command = buildCommand(
                action,
                value,
                DataHolder.getUserId(),
                parameters
        );
        setExecutionDetails(command);
        command.setAggregateId(parameters.getAggregateId());
        return command;
    }

    public Command getCommand(
            final String action,
            final String aggregateName,
            final Object value,
            final Parameters parameters
    ) {
        parameters.setUserId(DataHolder.getUserId());
        final Command command = buildCommand(
                action,
                aggregateName,
                value,
                DataHolder.getUserId(),
                parameters
        );
        setExecutionDetails(command);
        command.setAggregateId(parameters.getAggregateId());
        return command;
    }

    protected void setExecutor(final Command command, final Class<? extends BaseExecutor> executorClass) {
        command.setExecutor(DomainCommandFactory.applicationContext.getBean(executorClass));
    }

    private Command buildCommand(final BaseAction action, final Object value, final String executingUserId, final Parameters parameters) {
        parameters.setUserId(executingUserId);
        final Map<String, Object> info = new HashMap<>();
        setSender(parameters);
        return Command.builder()
                .action(action.getAction())
                .aggregateName(action.getAggregateName())
                .commandValue(value)
                .version(versionProvider.getVersion())
                .executingUserId(executingUserId)
                .parameters(parameters)
                .info(info)
                .build();
    }

    private Command buildCommand(final String action, final String aggregateName, final Object value, final String executingUserId, final Parameters parameters) {
        parameters.setUserId(executingUserId);
        final Map<String, Object> info = new HashMap<>();
        setSender(parameters);
        return Command.builder()
                .action(action)
                .aggregateName(aggregateName)
                .commandValue(value)
                .version(versionProvider.getVersion())
                .executingUserId(executingUserId)
                .parameters(parameters)
                .info(info)
                .build();
    }

    protected abstract void setExecutionDetails(Command command);

    private void setSender(final Parameters parameters) {
        for (final StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            if (!stackTraceElement.getClassName().startsWith("ch.ti8m.egov.framework")
                    && !stackTraceElement.getClassName().startsWith(Thread.class.getName())) {
                parameters.setSender(stackTraceElement.getClassName());
                break;
            }
        }
    }

}
