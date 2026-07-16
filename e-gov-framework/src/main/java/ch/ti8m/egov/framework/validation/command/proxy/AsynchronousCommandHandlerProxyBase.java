package ch.ti8m.egov.framework.validation.command.proxy;

import ch.ti8m.egov.framework.validation.command.Command;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AsynchronousCommandHandlerProxyBase implements CommandHandlerProxy {

    @Override
    public final Object handleCommand(final Command command) {
        CompletableFuture.supplyAsync(() -> {
            executeCommand(command);
            return true;
        }).thenRun(() -> {
            if (log.isInfoEnabled()) {
                log.info("Completed task of command " + command.getId());
            }
        });
        return true;
    }

    protected abstract void executeCommand(Command command);

}
