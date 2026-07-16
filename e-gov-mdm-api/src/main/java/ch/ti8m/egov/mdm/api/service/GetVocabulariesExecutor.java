package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.GetVocabulariesDto;
import ch.ti8m.egov.mdm.api.mapper.VocabularyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetVocabulariesExecutor implements Executor {

    @PrimaryRepository
    private final VocabularyRepository vocabularyRepository;

    private final VocabularyMapper vocabularyMapper;

    @Override
    public GetVocabulariesDto execute(final Command command) {
        return GetVocabulariesDto.builder()
                .vocabularies(vocabularyMapper.toListOfGetVocabularyDto(vocabularyRepository.findAll()))
                .build();
    }

}
