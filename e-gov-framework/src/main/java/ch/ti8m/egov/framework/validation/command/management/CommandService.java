package ch.ti8m.egov.framework.validation.command.management;


import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.CommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommandService {

    private final CommandRepository commandRepository;

    @Autowired
    public CommandService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    public List<Command> getAllCommands() {
        return commandRepository.findAll();
    }

}
