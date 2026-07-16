package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.mdm.persistence.entity.LanguageDefinition;
import org.springframework.stereotype.Repository;

@Repository
@ClassType(entityClass = LanguageDefinition.class)
public class VocabularyLanguageRepository extends BaseRepositoryImpl<LanguageDefinition> {
}
