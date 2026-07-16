package ch.ti8m.egov.mdm.persistence;

import ch.ti8m.egov.mdm.api.abstraction.Masterdata;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public abstract class MasterDataMapper {
    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "additionalContent", ignore = true)
    @Mapping(source = "vocabularyCode", target = "vocabularyCode")
    public abstract MasterDataGenericEntity createPersistenceBagFromEntity(Masterdata entity, String vocabularyCode);

    @Mapping(target = "vocabularyCode", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "additionalContent", ignore = true)
    public abstract MasterDataGenericEntity updatePersistenceBagFromEntity(@MappingTarget MasterDataGenericEntity persistenceBag, Masterdata entity);

    public <T extends Masterdata> T mapToEntity(final MasterDataGenericEntity persistenceBag, final Class<T> entityClass) {
        final T entity = objectMapper.convertValue(persistenceBag.getAdditionalContent(), entityClass);
        entity.setMasterDataGenericEntity(persistenceBag);
        return entity;
    }

    @AfterMapping
    protected void serializeMasterdata(final Masterdata entity, @MappingTarget final MasterDataGenericEntity persistenceBag) {
        persistenceBag.setAdditionalContent(
                objectMapper.convertValue(entity, new TypeReference<>() {}));
    }

    @AfterMapping
    protected void connectPersistenceBagToEntity(final Masterdata entity, @MappingTarget final MasterDataGenericEntity persistenceBag) {
        entity.setMasterDataGenericEntity(persistenceBag);
    }

}
