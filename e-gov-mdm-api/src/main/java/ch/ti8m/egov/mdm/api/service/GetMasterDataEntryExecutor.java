package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.mapper.MasterDataEntryMapper;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.ENTRY_CODE;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VALID_AT;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static ch.ti8m.egov.mdm.api.exception.MdmExceptionCodes.MDM_TO_MANY_RESULTS_FOUND;
import static ch.ti8m.egov.mdm.api.service.ResponseMessages.TO_MANY_RESULTS_MESSAGE;

@Component
@RequiredArgsConstructor
public class GetMasterDataEntryExecutor implements Executor {

    @PrimaryRepository
    private final MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    private final MasterDataEntryMapper masterDataEntryMapper;

    @Override
    public GetMasterDataEntryDto execute(final Command command) {
        String vocabularyCode = command.getParameters().getParameterAs(VOCABULARY_CODE);
        Objects.requireNonNull(vocabularyCode, "Vocabulary code is required");
        String entryCode = command.getParameters().getParameterAs(ENTRY_CODE);
        Objects.requireNonNull(entryCode, "Entry code is required");
        LocalDateTime validityDate = command.getParameters().getParameterAs(VALID_AT);
        List<MasterDataGenericEntity> result = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                vocabularyCode, entryCode, validityDate);
        if (result.isEmpty()) {
            return null;
        } else if (result.size() == 1) {
            return masterDataEntryMapper.toGetMasterDataEntry(result.get(0));
        } else {
            final String message = String.format(TO_MANY_RESULTS_MESSAGE, vocabularyCode, entryCode, validityDate);
            throw new EGovException(MDM_TO_MANY_RESULTS_FOUND, message);
        }
    }

}
