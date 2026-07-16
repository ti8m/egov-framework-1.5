package ch.ti8m.egov.mdm.api.mapper;

import ch.ti8m.egov.mdm.api.dto.LanguageDefinitionDto;
import ch.ti8m.egov.mdm.persistence.entity.LanguageDefinition;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LanguageDefinitionMapper {

    LanguageDefinitionDto toLanguageDefinitionDto(final LanguageDefinition LanguageDefinition);

    List<LanguageDefinitionDto> toListOfLanguageDefinitionDto(final List<LanguageDefinition> LanguageDefinitionEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vocabulary", ignore = true)
    @Mapping(target = "vocabularycode", ignore = true)
    LanguageDefinition toEntity(final LanguageDefinitionDto LanguageDefinitionDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vocabularycode", ignore = true)
    @Mapping(target = "vocabulary", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    void updateEntity(LanguageDefinitionDto LanguageDefinitionDto, @MappingTarget LanguageDefinition vocabulary);

}
