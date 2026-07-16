package ch.ti8m.egov.mdm.api.controller;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.DomainCommandFactory;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.service.CreateMasterDataEntryExecutor;
import ch.ti8m.egov.mdm.api.service.CreateVocabularyExecutor;
import ch.ti8m.egov.mdm.api.service.DeleteMasterDataEntryExecutor;
import ch.ti8m.egov.mdm.api.service.DeleteVocabularyExecutor;
import ch.ti8m.egov.mdm.api.service.GetMasterDataEntriesExecutor;
import ch.ti8m.egov.mdm.api.service.GetMasterDataEntryExecutor;
import ch.ti8m.egov.mdm.api.service.GetVocabulariesExecutor;
import ch.ti8m.egov.mdm.api.service.GetVocabularyExecutor;
import ch.ti8m.egov.mdm.api.service.UpdateMasterDataEntryExecutor;
import ch.ti8m.egov.mdm.api.service.UpdateVocabularyExecutor;
import ch.ti8m.egov.mdm.api.validation.MasterDataAction;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MasterDataCommandFactory extends DomainCommandFactory {

    private static final String MASTER_DATA_DOMAIN = "Master Data";

    private static final Map<String, Pair<Class<? extends Executor>, Command.ExecutionPlan>> ACTION_MAPPINGS = Map.ofEntries(
            Map.entry(MasterDataAction.CREATE_VOCABULARY.getAction(), Pair.of(CreateVocabularyExecutor.class, Command.ExecutionPlan.VALIDATE_BEFORE_EXECUTION)),
            Map.entry(MasterDataAction.UPDATE_VOCABULARY.getAction(), Pair.of(UpdateVocabularyExecutor.class, Command.ExecutionPlan.VALIDATE_BEFORE_EXECUTION)),
            Map.entry(MasterDataAction.DELETE_VOCABULARY.getAction(), Pair.of(DeleteVocabularyExecutor.class, Command.ExecutionPlan.VALIDATE_BEFORE_EXECUTION)),
            Map.entry(MasterDataAction.GET_VOCABULARY.getAction(), Pair.of(GetVocabularyExecutor.class, Command.ExecutionPlan.VALIDATE_AFTER_EXECUTION)),
            Map.entry(MasterDataAction.GET_VOCABULARIES.getAction(), Pair.of(GetVocabulariesExecutor.class, Command.ExecutionPlan.VALIDATE_AFTER_EXECUTION)),
            Map.entry(MasterDataAction.CREATE_MASTER_DATA_ENTRY.getAction(), Pair.of(CreateMasterDataEntryExecutor.class, Command.ExecutionPlan.VALIDATE_BEFORE_EXECUTION)),
            Map.entry(MasterDataAction.UPDATE_MASTER_DATA_ENTRY.getAction(), Pair.of(UpdateMasterDataEntryExecutor.class, Command.ExecutionPlan.VALIDATE_BEFORE_EXECUTION)),
            Map.entry(MasterDataAction.DELETE_MASTER_DATA_ENTRY.getAction(), Pair.of(DeleteMasterDataEntryExecutor.class, Command.ExecutionPlan.VALIDATE_BEFORE_EXECUTION)),
            Map.entry(MasterDataAction.GET_VALID_MASTER_DATA_ENTRY.getAction(), Pair.of(GetMasterDataEntryExecutor.class, Command.ExecutionPlan.VALIDATE_AFTER_EXECUTION)),
            Map.entry(MasterDataAction.GET_VALID_MASTER_DATA_ENTRIES.getAction(), Pair.of(GetMasterDataEntriesExecutor.class, Command.ExecutionPlan.VALIDATE_AFTER_EXECUTION))
    );

    @Override
    public void setExecutionDetails(final Command command) {
        setExecutor(command, ACTION_MAPPINGS.get(command.getAction()).getFirst());
        command.setExecutionPlan(ACTION_MAPPINGS.get(command.getAction()).getSecond());
        command.setDomain(MASTER_DATA_DOMAIN);
    }

}
