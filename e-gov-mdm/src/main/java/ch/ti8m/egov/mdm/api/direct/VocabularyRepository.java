package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ClassType(entityClass = Vocabulary.class)
public class VocabularyRepository extends BaseRepositoryImpl<Vocabulary> {

    public List<Vocabulary> findByVocabularyCode(final String vocabularyCode) {
        return super.findWithFilter(Vocabulary.Fields.code + " == '" + vocabularyCode + "'");
    }

}
