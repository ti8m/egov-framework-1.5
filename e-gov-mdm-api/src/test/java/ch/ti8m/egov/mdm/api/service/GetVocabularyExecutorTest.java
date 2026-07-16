package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.GetVocabularyDto;
import ch.ti8m.egov.mdm.api.mapper.VocabularyMapper;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetVocabularyExecutorTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private VocabularyMapper vocabularyMapper;

    @InjectMocks
    private GetVocabularyExecutor getVocabularyExecutor;

    @Test
    void when_repositoryReturnsVocabulary_then_executeReturnsMappedDto() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .build();

        Vocabulary vocabularyEntity = Mockito.mock(Vocabulary.class);
        when(vocabularyRepository.findOneBy(eq(Vocabulary.Fields.code), eq("testVocabulary")))
                .thenReturn(Optional.of(vocabularyEntity));

        GetVocabularyDto mappedDto = Mockito.mock(GetVocabularyDto.class);
        when(vocabularyMapper.toGetVocabularyDto(vocabularyEntity))
                .thenReturn(mappedDto);

        // act
        GetVocabularyDto result = getVocabularyExecutor.execute(command);

        // assert
        assertThat(result).isNotNull().isEqualTo(mappedDto);
        Mockito.verify(vocabularyRepository, times(1))
                .findOneBy(eq(Vocabulary.Fields.code), eq("testVocabulary"));
        Mockito.verify(vocabularyMapper, times(1))
                .toGetVocabularyDto(vocabularyEntity);
    }

    @Test
    void when_repositoryReturnsEmptyOptional_then_IllegalArgumentExceptionIsThrown() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "unknownVocabulary")
                        .build())
                .build();

        when(vocabularyRepository.findOneBy(eq(Vocabulary.Fields.code), eq("unknownVocabulary")))
                .thenReturn(Optional.empty());

        // act and assert
        assertThrows(IllegalArgumentException.class,
                () -> getVocabularyExecutor.execute(command));

        // Mapper darf hier nicht aufgerufen werden
        verifyNoInteractions(vocabularyMapper);
    }

    @Test
    void when_repositoryThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .build();

        when(vocabularyRepository.findOneBy(eq(Vocabulary.Fields.code), eq("testVocabulary")))
                .thenThrow(new RuntimeException("db error"));

        // act and assert
        assertThrows(RuntimeException.class,
                () -> getVocabularyExecutor.execute(command));

        verifyNoInteractions(vocabularyMapper);
    }

    @Test
    void when_mapperThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .build();

        Vocabulary vocabularyEntity = Mockito.mock(Vocabulary.class);
        when(vocabularyRepository.findOneBy(eq(Vocabulary.Fields.code), eq("testVocabulary")))
                .thenReturn(Optional.of(vocabularyEntity));

        when(vocabularyMapper.toGetVocabularyDto(vocabularyEntity))
                .thenThrow(new RuntimeException("mapping error"));

        // act and assert
        assertThrows(RuntimeException.class,
                () -> getVocabularyExecutor.execute(command));
    }

    @Test
    void when_vocabularyCodeMissingInCommand_then_NullPointerExceptionIsThrown() {
        // arrange: Command ohne VOCABULARY_CODE
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .build())
                .build();

        // act and assert
        assertThrows(NullPointerException.class,
                () -> getVocabularyExecutor.execute(command));

        verifyNoInteractions(vocabularyRepository);
        verifyNoInteractions(vocabularyMapper);
    }

}