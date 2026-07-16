package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.UpdateVocabularyDto;
import ch.ti8m.egov.mdm.api.mapper.VocabularyMapper;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static ch.ti8m.egov.mdm.api.exception.MdmExceptionCodes.MDM_TO_MANY_RESULTS_FOUND;
import static ch.ti8m.egov.mdm.api.service.ResponseMessages.TO_MANY_RESULTS_MESSAGE;
import static ch.ti8m.egov.mdm.api.service.ResponseMessages.VOCABULARY_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class UpdateVocabularyExecutor implements Executor {

    @PrimaryRepository
    private final VocabularyRepository vocabularyRepository;

    private final VocabularyMapper vocabularyMapper;

    @Override
    public Void execute(final Command command) {
        String vocabularyCode = command.getParameters().getParameterAs(VOCABULARY_CODE);
        Objects.requireNonNull(vocabularyCode, "Vocabulary code is required");
        UpdateVocabularyDto vocabularyData = command.unwrap();
        final List<Vocabulary> currentValidVocabularies = vocabularyRepository.findByVocabularyCode(vocabularyCode);
        if (currentValidVocabularies.isEmpty()) {
            throw new IllegalArgumentException(VOCABULARY_NOT_FOUND);
        } else if (currentValidVocabularies.size() == 1) {
            Vocabulary vocabularyToUpdate = currentValidVocabularies.get(0);
            vocabularyMapper.updateEntity(vocabularyData, vocabularyToUpdate);
            vocabularyRepository.update(vocabularyToUpdate);
        } else {
            final String message = String.format(TO_MANY_RESULTS_MESSAGE, vocabularyCode, vocabularyCode, LocalDateTime.now());
            throw new EGovException(MDM_TO_MANY_RESULTS_FOUND, message);
        }
        return null;
    }

}
