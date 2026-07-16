package ch.ti8m.egov.demo.domain.gescheaft.validation;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.handler.CommandHandlerFactory;
import ch.ti8m.egov.framework.validation.command.proxy.CommandHandlerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GescheaftApplicationServiceProxy implements CommandHandlerProxy {

    private final CommandHandlerFactory commandHandlerFactory;

    @Autowired
    public GescheaftApplicationServiceProxy(
            final CommandHandlerFactory commandHandlerFactory
    ) {
        this.commandHandlerFactory = commandHandlerFactory;
    }

    @Override
    public <T> T handleCommand(final Command command) {
        return (T) commandHandlerFactory.getCommandHandler(GescheaftApplicationService.class).handleCommand(command);
    }

}
