package ch.ti8m.egov.mdm.api.validation;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.handler.CommandHandlerFactory;
import ch.ti8m.egov.framework.validation.command.proxy.CommandHandlerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MasterDataApplicationServiceProxy implements CommandHandlerProxy {

    private final CommandHandlerFactory commandHandlerFactory;

    @Autowired
    public MasterDataApplicationServiceProxy(
            final CommandHandlerFactory commandHandlerFactory
    ) {
        this.commandHandlerFactory = commandHandlerFactory;
    }

    @Override
    public <T> T handleCommand(final Command command) {
        return (T) commandHandlerFactory.getCommandHandler(MasterDataApplicationService.class).handleCommand(command);
    }

}
