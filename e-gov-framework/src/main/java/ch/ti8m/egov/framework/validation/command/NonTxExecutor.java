package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.validation.command.executor.BaseExecutor;

public interface NonTxExecutor extends BaseExecutor {

    @Override
    Object execute(Command command);

}
