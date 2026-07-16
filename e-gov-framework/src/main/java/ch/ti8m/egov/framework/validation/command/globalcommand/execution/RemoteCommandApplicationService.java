package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.handler.CommandHandlerBase;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import org.springframework.stereotype.Component;

@Component
public class RemoteCommandApplicationService extends CommandHandlerBase<Command> {
    @Override
    protected void loadAggregate(Command command) {

    }

    @Override
    protected ValidationMethodMapper provideValidationMethodMapper() {
        return null;
    }
}
