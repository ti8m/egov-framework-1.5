package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.dto.UpdateMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.mapper.MasterDataEntryMapper;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.ENTRY_CODE;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static ch.ti8m.egov.mdm.api.exception.MdmExceptionCodes.MDM_TO_MANY_RESULTS_FOUND;
import static ch.ti8m.egov.mdm.api.service.ResponseMessages.ENTRY_NOT_FOUND;
import static ch.ti8m.egov.mdm.api.service.ResponseMessages.TO_MANY_RESULTS_MESSAGE;

@Component
@RequiredArgsConstructor
public class UpdateMasterDataEntryExecutor implements Executor {

    @PrimaryRepository
    private final MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    private final MasterDataEntryMapper masterDataEntryMapper;

    @Override
    public Void execute(final Command command) {
        String vocabularyCode = command.getParameters().getParameterAs(VOCABULARY_CODE);
        Objects.requireNonNull(vocabularyCode, "Vocabulary code is required");
        String entryCode = command.getParameters().getParameterAs(ENTRY_CODE);
        Objects.requireNonNull(entryCode, "Entry code is required");
        UpdateMasterDataEntryDto entryData = command.unwrap();
        List<MasterDataGenericEntity> currentValidEntries = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                vocabularyCode, entryCode, LocalDateTime.now());
        if (currentValidEntries.isEmpty()) {
            throw new IllegalArgumentException(ENTRY_NOT_FOUND);
        } else if (currentValidEntries.size() == 1) {
            MasterDataGenericEntity codeToUpdate = currentValidEntries.get(0);
            masterDataEntryMapper.updateEntity(entryData, codeToUpdate);
            masterDataGenericEntityRepository.update(codeToUpdate);
        } else {
            final String message = String.format(TO_MANY_RESULTS_MESSAGE, vocabularyCode, entryCode, LocalDateTime.now());
            throw new EGovException(MDM_TO_MANY_RESULTS_FOUND, message);
        }
        return null;
    }

}
