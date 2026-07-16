package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.mapper.VocabularyMapper;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateVocabularyExecutor implements Executor {

    @PrimaryRepository
    private final VocabularyRepository vocabularyRepository;

    private final VocabularyMapper vocabularyMapper;

    @Override
    public Long execute(final Command command) {
        final Vocabulary vocabulary = vocabularyMapper.toEntity(command.unwrap());
        vocabularyRepository.save(vocabulary);
        return vocabulary.getId();
    }

}
