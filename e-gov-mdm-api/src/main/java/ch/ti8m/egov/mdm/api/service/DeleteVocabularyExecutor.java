package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static ch.ti8m.egov.mdm.api.exception.MdmExceptionCodes.MDM_TO_MANY_RESULTS_FOUND;
import static ch.ti8m.egov.mdm.api.service.ResponseMessages.TO_MANY_RESULTS_MESSAGE;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyExecutor implements Executor {

    @PrimaryRepository
    private final VocabularyRepository vocabularyRepository;

    @Override
    public Void execute(final Command command) {
        String vocabularyCode = command.getParameters().getParameterAs(VOCABULARY_CODE);
        final List<Vocabulary> currentValidVocabularies = vocabularyRepository.findBy(Vocabulary.Fields.code, vocabularyCode);
        if (currentValidVocabularies.isEmpty()) {
            // entry does not exist (maybe it was already deleted), so we silently return
            return null;
        } else if (currentValidVocabularies.size() == 1) {
            Vocabulary vocabularyToDelete = currentValidVocabularies.get(0);
            vocabularyRepository.delete(vocabularyToDelete);
        } else {
            final String message = String.format(TO_MANY_RESULTS_MESSAGE, vocabularyCode, vocabularyCode, LocalDateTime.now());
            throw new EGovException(MDM_TO_MANY_RESULTS_FOUND, message);
        }
        return null;
    }

}
