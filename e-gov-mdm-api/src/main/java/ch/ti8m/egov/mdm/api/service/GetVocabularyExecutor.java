package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.GetVocabularyDto;
import ch.ti8m.egov.mdm.api.mapper.VocabularyMapper;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;

@Component
@RequiredArgsConstructor
public class GetVocabularyExecutor implements Executor {

    @PrimaryRepository
    private final VocabularyRepository vocabularyRepository;

    private final VocabularyMapper vocabularyMapper;

    @Override
    public GetVocabularyDto execute(final Command command) {
        String vocabularyCode = command.getParameters().getParameterAs(VOCABULARY_CODE);
        Objects.requireNonNull(vocabularyCode, "Vocabulary code is required");
        return vocabularyRepository.findOneBy(Vocabulary.Fields.code, vocabularyCode)
                .map(vocabularyMapper::toGetVocabularyDto)
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found"));
    }

}
