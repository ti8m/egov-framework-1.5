package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntriesDto;
import ch.ti8m.egov.mdm.api.mapper.MasterDataEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.ENTRY_CODE;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;

@Component
@RequiredArgsConstructor
public class GetMasterDataEntriesExecutor implements Executor {

    @PrimaryRepository
    private final MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    private final MasterDataEntryMapper masterDataEntryMapper;

    @Override
    public GetMasterDataEntriesDto execute(final Command command) {
        String vocabularyCode = command.getParameters().getParameterAs(VOCABULARY_CODE);
        Objects.requireNonNull(vocabularyCode, "Vocabulary code is required");
        LocalDateTime validityDate = command.getParameters().getParameterAs(MasterDataParameterNames.VALID_AT);
        Boolean withHistory = command.getParameters().getParameter(MasterDataParameterNames.WITH_HISTORY) != null ?
                command.getParameters().getParameterAs(MasterDataParameterNames.WITH_HISTORY) : Boolean.FALSE;
        if (Boolean.TRUE.equals(withHistory)) {
            String entryCode = command.getParameters().getParameterAs(ENTRY_CODE);
            Objects.requireNonNull(vocabularyCode, "Entry code is required for queries with history");
            return GetMasterDataEntriesDto.builder()
                    .entries(masterDataEntryMapper.toListOfGetMasterDataEntry(
                            masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCodeWithHistory(vocabularyCode, entryCode)))
                    .build();
        } else {
            return GetMasterDataEntriesDto.builder()
                    .entries(masterDataEntryMapper.toListOfGetMasterDataEntry(
                            masterDataGenericEntityRepository.findValidByVocabularyCode(vocabularyCode, validityDate)))
                    .build();
        }
    }

}
