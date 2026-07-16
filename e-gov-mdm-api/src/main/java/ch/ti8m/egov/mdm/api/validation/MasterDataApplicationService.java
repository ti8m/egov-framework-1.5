package ch.ti8m.egov.mdm.api.validation;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.handler.CommandHandlerBase;
import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MasterDataApplicationService extends CommandHandlerBase<Vocabulary> implements MasterDataApplicationServiceValidation {

    private final VocabularyRepository vocabularyRepository;

    @Autowired
    public MasterDataApplicationService(
            final VocabularyRepository vocabularyRepository
    ) {
        this.vocabularyRepository = vocabularyRepository;
    }

    @Override
    protected void loadAggregate(final Command command) {
        super.setAggregate(vocabularyRepository.findById(command.getAggregateId())
                .orElseThrow(() -> new IllegalArgumentException("Vocabulary not found")));
    }

    @Override
    protected ValidationMethodMapper provideValidationMethodMapper() {
        return new MasterDataValidationMethodMapperImpl(this);
    }

}
