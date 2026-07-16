package ch.ti8m.egov.framework.validation.command.executor;

import ch.ti8m.egov.framework.validation.command.Command;

public interface BaseExecutor {
    Object execute(Command command);
}
