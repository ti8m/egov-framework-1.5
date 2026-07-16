package ch.ti8m.egov.mdm.api.mapper;

import ch.ti8m.egov.mdm.api.dto.CreateVocabularyDto;
import ch.ti8m.egov.mdm.api.dto.GetVocabularyDto;
import ch.ti8m.egov.mdm.api.dto.UpdateVocabularyDto;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LanguageDefinitionMapper.class, FieldDefinitionMapper.class, VocabularyLnMapper.class})
public interface VocabularyMapper {

    GetVocabularyDto toGetVocabularyDto(final Vocabulary vocabularyEntity);

    List<GetVocabularyDto> toListOfGetVocabularyDto(final List<Vocabulary> vocabularyEntities);

    @Mapping(target = "id", ignore = true)
    Vocabulary toEntity(final CreateVocabularyDto createVocabularyDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    void updateEntity(UpdateVocabularyDto updateVocabularyDto, @MappingTarget Vocabulary vocabulary);

}
