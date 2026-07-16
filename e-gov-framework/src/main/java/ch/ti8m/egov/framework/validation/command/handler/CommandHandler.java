package ch.ti8m.egov.framework.validation.command.handler;

import ch.ti8m.egov.framework.validation.command.Command;

public interface CommandHandler {

    Object handleCommand(Command command);

}
