package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.CreateVocabularyDto;
import ch.ti8m.egov.mdm.api.mapper.VocabularyMapper;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateVocabularyExecutorTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private VocabularyMapper vocabularyMapper;

    @InjectMocks
    private CreateVocabularyExecutor createVocabularyExecutor;

    @Test
    void when_mapperAndRepositoryMockedCorrectly_then_executeReturnsIdAndSaveGetsCalled() {
        // arrange
        final CreateVocabularyDto createVocabulary = Mockito.mock(CreateVocabularyDto.class);
        final Command command = Command.builder()
                .commandValue(createVocabulary)
                .build();
        final Vocabulary vocabulary = Mockito.mock(Vocabulary.class);
        when(vocabularyMapper.toEntity(createVocabulary)).thenReturn(vocabulary);
        Long ID = 1L;
        when(vocabulary.getId()).thenReturn(ID);
        doNothing().when(vocabularyRepository).save(isA(Vocabulary.class));
        // act
        Long id = createVocabularyExecutor.execute(command);
        // assert
        assertThat(id).isNotNull().isEqualTo(ID);
        verify(vocabularyRepository, times(1)).save(vocabulary);
    }

    @Test
    void when_mapperThrowsARuntimeException_then_saveHasNoInteraction() {
        // arrange
        final CreateVocabularyDto createVocabulary = Mockito.mock(CreateVocabularyDto.class);
        final Command command = Command.builder()
                .commandValue(createVocabulary)
                .build();
        final Vocabulary vocabulary = Mockito.mock(Vocabulary.class);
        when(vocabularyMapper.toEntity(createVocabulary)).thenThrow(new RuntimeException("RuntimeException in mapper"));
        // act and assert
        assertThrows(RuntimeException.class, () -> createVocabularyExecutor.execute(command));
        verify(vocabularyRepository, times(0)).save(vocabulary);
    }

    @Test
    void when_repositoryThrowsARuntimeException_then_getIdHasNoInteraction() {
        // arrange
        final CreateVocabularyDto createVocabulary = Mockito.mock(CreateVocabularyDto.class);
        final Command command = Command.builder()
                .commandValue(createVocabulary)
                .build();
        final Vocabulary vocabulary = Mockito.mock(Vocabulary.class);
        when(vocabularyMapper.toEntity(createVocabulary)).thenReturn(vocabulary);
        doThrow(RuntimeException.class).when(vocabularyRepository)
                .save(isA(Vocabulary.class));
        // act and assert
        assertThrows(RuntimeException.class, () -> createVocabularyExecutor.execute(command));
        verify(vocabulary, times(0)).getId();
    }

}