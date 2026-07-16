package ch.ti8m.egov.mdm.api.mapper;

import ch.ti8m.egov.mdm.api.dto.CreateMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.dto.UpdateMasterDataEntryDto;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MasterDataEntryMapper {

    GetMasterDataEntryDto toGetMasterDataEntry(final MasterDataGenericEntity masterDataGenericEntity);

    List<GetMasterDataEntryDto> toListOfGetMasterDataEntry(final List<MasterDataGenericEntity> masterDataGenericEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "archived", ignore = true)
    MasterDataGenericEntity toEntity(final CreateMasterDataEntryDto createMasterDataEntryDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "vocabularyCode", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "languageCode", ignore = true)
    @Mapping(target = "archived", ignore = true)
    void updateEntity(UpdateMasterDataEntryDto updateMasterDataEntryDto, @MappingTarget MasterDataGenericEntity masterDataGenericEntity);

}
