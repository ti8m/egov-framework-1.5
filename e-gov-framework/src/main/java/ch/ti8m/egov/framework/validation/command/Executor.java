package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.validation.command.executor.BaseExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface Executor extends BaseExecutor {

    @Override
    @Transactional
    Object execute(Command command);

}
