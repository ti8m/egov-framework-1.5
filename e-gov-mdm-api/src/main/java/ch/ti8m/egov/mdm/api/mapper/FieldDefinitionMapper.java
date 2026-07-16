package ch.ti8m.egov.mdm.api.mapper;

import ch.ti8m.egov.mdm.api.dto.FieldDefinitionDto;
import ch.ti8m.egov.mdm.persistence.entity.FieldDefinition;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {FieldDefinitionLnMapper.class})
public interface FieldDefinitionMapper {

    FieldDefinitionDto toFieldDefinitionDto(final FieldDefinition FieldDefinition);

    List<FieldDefinitionDto> toListOfFieldDefinitionDto(final List<FieldDefinition> FieldDefinitionEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vocabulary", ignore = true)
    FieldDefinition toEntity(final FieldDefinitionDto FieldDefinitionDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vocabulary", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    void updateEntity(FieldDefinitionDto FieldDefinitionDto, @MappingTarget FieldDefinition vocabulary);

}
