package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@ClassType(entityClass = MasterDataGenericEntity.class)
public class MasterDataGenericEntityRepository extends BaseRepositoryImpl<MasterDataGenericEntity> {

    public List<MasterDataGenericEntity> findValidByVocabularyCode(
            final String vocabularyCode,
            final LocalDateTime validAt) {

        return super.findWithFilter(
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + vocabularyCode + "'",
                MasterDataGenericEntity.Fields.validFrom + " <= " + validAt,
                MasterDataGenericEntity.Fields.validTo + " >= " + validAt
        );
    }

    public List<MasterDataGenericEntity> findValidByVocabularyCodeAndEntryCode(
            final String vocabularyCode,
            final String entryCode,
            final LocalDateTime validAt) {

        return super.findWithFilter(
                MasterDataGenericEntity.Fields.code + " == '" + entryCode + "'",
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + vocabularyCode + "'",
                MasterDataGenericEntity.Fields.validFrom + " <= " + validAt,
                MasterDataGenericEntity.Fields.validTo + " >= " + validAt
        );
    }

    public List<MasterDataGenericEntity> findValidByVocabularyCodeAndEntryCodeWithHistory(
            final String vocabularyCode,
            final String entryCode) {

        return super.findWithFilter(
                entryCode == null ? null : MasterDataGenericEntity.Fields.code + " == '" + entryCode + "'",
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + vocabularyCode + "'"
        );
    }

}
