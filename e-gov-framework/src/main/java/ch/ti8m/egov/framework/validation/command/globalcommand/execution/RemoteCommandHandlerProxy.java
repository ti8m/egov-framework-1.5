package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.handler.CommandHandlerFactory;
import ch.ti8m.egov.framework.validation.command.proxy.CommandHandlerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RemoteCommandHandlerProxy implements CommandHandlerProxy {
    private final CommandHandlerFactory commandHandlerFactory;

    @Autowired
    public RemoteCommandHandlerProxy(CommandHandlerFactory commandHandlerFactory) {
        this.commandHandlerFactory = commandHandlerFactory;
    }

    @Override
    public Object handleCommand(Command command) {
        return commandHandlerFactory.getCommandHandler(RemoteCommandApplicationService.class).handleCommand(command);
    }
}
