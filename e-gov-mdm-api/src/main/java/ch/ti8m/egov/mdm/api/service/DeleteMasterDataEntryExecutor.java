package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.ENTRY_CODE;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static ch.ti8m.egov.mdm.api.exception.MdmExceptionCodes.MDM_TO_MANY_RESULTS_FOUND;
import static ch.ti8m.egov.mdm.api.service.ResponseMessages.TO_MANY_RESULTS_MESSAGE;

@Component
@RequiredArgsConstructor
public class DeleteMasterDataEntryExecutor implements Executor {

    @PrimaryRepository
    private final MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    @Override
    public Void execute(final Command command) {
        String vocabularyCode = command.getParameters().getParameterAs(VOCABULARY_CODE);
        String entryCode = command.getParameters().getParameterAs(ENTRY_CODE);
        List<MasterDataGenericEntity> currentValidCodes = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                vocabularyCode, entryCode, LocalDateTime.now());
        if (currentValidCodes.isEmpty()) {
            // entry does not exist (maybe it was already deleted), so we silently return
            return null;
        } else if (currentValidCodes.size() == 1) {
            MasterDataGenericEntity codeToDelete = currentValidCodes.get(0);
            masterDataGenericEntityRepository.delete(codeToDelete);
        } else {
            final String message = String.format(TO_MANY_RESULTS_MESSAGE, vocabularyCode, entryCode, LocalDateTime.now());
            throw new EGovException(MDM_TO_MANY_RESULTS_FOUND, message);
        }
        return null;
    }

}
