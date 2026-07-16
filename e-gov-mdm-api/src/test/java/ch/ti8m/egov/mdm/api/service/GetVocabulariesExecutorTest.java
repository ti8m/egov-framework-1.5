package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.GetVocabulariesDto;
import ch.ti8m.egov.mdm.api.dto.GetVocabularyDto;
import ch.ti8m.egov.mdm.api.mapper.VocabularyMapper;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetVocabulariesExecutorTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private VocabularyMapper vocabularyMapper;

    @InjectMocks
    private GetVocabulariesExecutor getVocabulariesExecutor;

    @Test
    void when_repositoryAndMapperReturnValidData_then_executeReturnsBuiltDto() {
        // arrange
        final Command command = Command.builder().build();

        Vocabulary vocabularyEntity = Mockito.mock(Vocabulary.class);
        when(vocabularyRepository.findAll())
                .thenReturn(List.of(vocabularyEntity));

        GetVocabularyDto getVocabulary = Mockito.mock(GetVocabularyDto.class);
        List<GetVocabularyDto> mappedList = List.of(getVocabulary);
        when(vocabularyMapper.toListOfGetVocabularyDto(List.of(vocabularyEntity)))
                .thenReturn(mappedList);

        // act
        GetVocabulariesDto result = getVocabulariesExecutor.execute(command);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getVocabularies()).isNotNull().hasSize(1).containsExactly(getVocabulary);
        Mockito.verify(vocabularyRepository, times(1)).findAll();
        Mockito.verify(vocabularyMapper, times(1))
                .toListOfGetVocabularyDto(List.of(vocabularyEntity));
    }

    @Test
    void when_repositoryReturnsEmptyList_then_executeReturnsDtoWithEmptyList() {
        // arrange
        final Command command = Command.builder().build();

        when(vocabularyRepository.findAll())
                .thenReturn(Collections.emptyList());
        when(vocabularyMapper.toListOfGetVocabularyDto(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        // act
        GetVocabulariesDto result = getVocabulariesExecutor.execute(command);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getVocabularies()).isNotNull().isEmpty();
        Mockito.verify(vocabularyRepository, times(1)).findAll();
        Mockito.verify(vocabularyMapper, times(1))
                .toListOfGetVocabularyDto(Collections.emptyList());
    }

    @Test
    void when_repositoryThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        final Command command = Command.builder().build();

        when(vocabularyRepository.findAll())
                .thenThrow(new RuntimeException("db error"));

        // act and assert
        assertThrows(RuntimeException.class,
                () -> getVocabulariesExecutor.execute(command));

        // Mapper darf in diesem Fall nicht aufgerufen werden
        verifyNoInteractions(vocabularyMapper);
    }

    @Test
    void when_mapperThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        final Command command = Command.builder().build();

        Vocabulary vocabularyEntity = Mockito.mock(Vocabulary.class);
        when(vocabularyRepository.findAll())
                .thenReturn(List.of(vocabularyEntity));

        when(vocabularyMapper.toListOfGetVocabularyDto(List.of(vocabularyEntity)))
                .thenThrow(new RuntimeException("mapping error"));

        // act and assert
        assertThrows(RuntimeException.class,
                () -> getVocabulariesExecutor.execute(command));
    }

}