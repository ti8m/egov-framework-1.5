package ch.ti8m.egov.mdm.api.abstraction;

import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.persistence.MasterDataMapper;
import ch.ti8m.egov.mdm.persistence.description.MDVocabulary;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

public abstract class MasterDataRepository<T extends Masterdata> {

    @Autowired
    private MasterDataGenericEntityRepository masterDataGenericEntityRepository;
    @Autowired
    private MasterDataMapper masterdataMapper;

    @Transactional
    public Long saveWithTX(final T entity) {
        final MasterDataGenericEntity MasterDataGenericEntity = masterdataMapper.createPersistenceBagFromEntity(entity, getVocabularyCode());
        return masterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity);
    }

    @Transactional
    public T updateWithTX(final T entity) {
        final MasterDataGenericEntity MasterDataGenericEntity = masterdataMapper.updatePersistenceBagFromEntity(entity.getMasterDataGenericEntity(), entity);
        masterDataGenericEntityRepository.updateWithTx(MasterDataGenericEntity);
        return entity;
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return findWithFilter();
    }

    @Transactional(readOnly = true)
    public List<T> findForLanguage(final String language) {
        return findWithFilter(MasterDataGenericEntity.Fields.languageCode + " == '" + language + "'");
    }

    @Transactional(readOnly = true)
    public List<T> findWithFilter(final String... filter) {
        final String[] filters = Arrays.copyOf(filter, filter.length + 1);
        filters[filters.length - 1] = MasterDataGenericEntity.Fields.vocabularyCode + " == '" + getVocabularyCode() + "'";
        return masterDataGenericEntityRepository.findWithFilter(filters)
                .stream()
                .map(MasterDataGenericEntity -> masterdataMapper.mapToEntity(MasterDataGenericEntity, getMasterdataClass()))
                .toList();
    }

    @Transactional
    public void deleteAll() {
        masterDataGenericEntityRepository.deleteWithTx(masterDataGenericEntityRepository.findBy(MasterDataGenericEntity.Fields.vocabularyCode, getVocabularyCode()));
    }

    private String getVocabularyCode() {
        return getMasterdataClass().getAnnotation(MDVocabulary.class).code();
    }

    private Class<T> getMasterdataClass() {
        return AopProxyUtils.ultimateTargetClass(this).getAnnotation(ClassType.class).entityClass();
    }

}
