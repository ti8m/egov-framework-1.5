package ch.ti8m.egov.demo.domain.gescheaft.controller;

import ch.ti8m.egov.demo.domain.gescheaft.service.CreateGescheaftExecutor;
import ch.ti8m.egov.demo.domain.gescheaft.validation.GescheaftAction;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.DomainCommandFactory;
import ch.ti8m.egov.framework.validation.command.Executor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GescheaftCommandFactory extends DomainCommandFactory {

    private static final String GESCHEAFT_DOMAIN = "Gescheaft";

    private static final Map<String, Pair<Class<? extends Executor>, Command.ExecutionPlan>> ACTION_MAPPINGS = Map.ofEntries(
            Map.entry(GescheaftAction.CREATE_GESCHEAFT.getAction(), Pair.of(CreateGescheaftExecutor.class, Command.ExecutionPlan.VALIDATE_BEFORE_EXECUTION))
    );

    @Override
    public void setExecutionDetails(final Command command) {
        setExecutor(command, ACTION_MAPPINGS.get(command.getAction()).getFirst());
        command.setExecutionPlan(ACTION_MAPPINGS.get(command.getAction()).getSecond());
        command.setDomain(GESCHEAFT_DOMAIN);
    }

}
