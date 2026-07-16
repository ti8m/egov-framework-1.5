package ch.ti8m.egov.framework.validation.command.proxy;

import ch.ti8m.egov.framework.validation.command.Command;

public interface CommandHandlerProxy {

    <T> T handleCommand(Command command);

}
