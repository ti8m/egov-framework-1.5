package ch.ti8m.egov.mdm.api.mapper;

import ch.ti8m.egov.mdm.api.dto.VocabularyLnDto;
import ch.ti8m.egov.mdm.persistence.entity.VocabularyLn;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VocabularyLnMapper {

    VocabularyLnDto toVocabularyLnDto(final VocabularyLn vocabularyLn);

    List<VocabularyLnDto> toListOfVocabularyLnDto(final List<VocabularyLn> vocabularyLnEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vocabulary", ignore = true)
    VocabularyLn toEntity(final VocabularyLnDto vocabularyLnDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "vocabulary", ignore = true)
    void updateEntity(VocabularyLnDto vocabularyLnDto, @MappingTarget VocabularyLn vocabulary);

}
