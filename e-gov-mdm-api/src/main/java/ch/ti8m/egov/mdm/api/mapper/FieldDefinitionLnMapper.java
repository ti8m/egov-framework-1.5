package ch.ti8m.egov.mdm.api.mapper;

import ch.ti8m.egov.mdm.api.dto.FieldDefinitionLnDto;
import ch.ti8m.egov.mdm.persistence.entity.FieldDefinitionLn;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FieldDefinitionLnMapper {

    FieldDefinitionLnDto toFieldDefinitionLnDto(final FieldDefinitionLn FieldDefinitionLn);

    List<FieldDefinitionLnDto> toListOfFieldDefinitionLnDto(final List<FieldDefinitionLn> FieldDefinitionLnEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "field", ignore = true)
    FieldDefinitionLn toEntity(final FieldDefinitionLnDto FieldDefinitionLnDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "field", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    void updateEntity(FieldDefinitionLnDto FieldDefinitionLnDto, @MappingTarget FieldDefinitionLn vocabulary);

}
