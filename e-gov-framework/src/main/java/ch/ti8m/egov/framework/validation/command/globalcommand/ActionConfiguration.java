package ch.ti8m.egov.framework.validation.command.globalcommand;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.executor.BaseExecutor;
import ch.ti8m.egov.framework.validation.command.proxy.CommandHandlerProxy;
import lombok.Data;

@Data
public class ActionConfiguration {

    private String action;
    private String domain;
    private Class<? extends BaseExecutor> executorClass;
    private Command.ExecutionPlan executionPlan;
    private Class<? extends CommandHandlerProxy> commandHandlerProxyClass;
    private Type type;

    private ActionConfiguration(
            final String action,
            final String domain,
            final Class<? extends BaseExecutor> executorClass,
            final Command.ExecutionPlan executionPlan,
            final Class<? extends CommandHandlerProxy> commandHandlerProxyClass,
            final Type type
    ) {
        this.action = action;
        this.domain = domain;
        this.executorClass = executorClass;
        this.executionPlan = executionPlan;
        this.commandHandlerProxyClass = commandHandlerProxyClass;
        this.type = type;
    }

    public static ActionConfiguration of(
            final String action,
            final Class<? extends BaseExecutor> executorClass,
            final Command.ExecutionPlan executionPlan,
            final Class<? extends CommandHandlerProxy> commandHandlerProxyClass
    ) {
        return new ActionConfiguration(
                action,
                null,
                executorClass,
                executionPlan,
                commandHandlerProxyClass,
                Type.GLOBAL
        );
    }

    public static ActionConfiguration of(
            final String action,
            final String domain,
            final Class<? extends BaseExecutor> executorClass,
            final Command.ExecutionPlan executionPlan,
            final Class<? extends CommandHandlerProxy> commandHandlerProxyClass
    ) {
        return new ActionConfiguration(
                action,
                domain,
                executorClass,
                executionPlan,
                commandHandlerProxyClass,
                Type.GLOBAL
        );
    }

    public static ActionConfiguration local(
            final String action,
            final Class<? extends BaseExecutor> executorClass,
            final Command.ExecutionPlan executionPlan,
            final Class<? extends CommandHandlerProxy> commandHandlerProxyClass
    ) {
        return new ActionConfiguration(
                action,
                null,
                executorClass,
                executionPlan,
                commandHandlerProxyClass,
                Type.LOCAL
        );
    }

    public enum Type {
        LOCAL,
        GLOBAL
    }

}
