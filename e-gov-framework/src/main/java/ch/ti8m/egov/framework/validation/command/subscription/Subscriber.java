package ch.ti8m.egov.framework.validation.command.subscription;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.validation.command.Command;

public abstract class Subscriber {

    public void execute(
            final Command command,
            final String executingUserId
    ) {
        try {
            DataHolder.setUserId(executingUserId);
            DataHolder.pushCommandId(command.getId());
            execute(command);
            DataHolder.popCommandId();
        } finally {
            DataHolder.cleanUp();
        }
    }

    public abstract void doSubscribe();

    protected abstract void execute(Command command);

}
