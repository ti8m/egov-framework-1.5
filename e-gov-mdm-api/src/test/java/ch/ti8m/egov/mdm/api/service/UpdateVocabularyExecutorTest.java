package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.UpdateVocabularyDto;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.mdm.api.mapper.VocabularyMapper;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static ch.ti8m.egov.mdm.api.service.ResponseMessages.VOCABULARY_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateVocabularyExecutorTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @Mock
    private VocabularyMapper vocabularyMapper;

    @InjectMocks
    private UpdateVocabularyExecutor updateVocabularyExecutor;

    @Test
    void when_singleCurrentValidCode_then_entityIsUpdatedAndSaved() {
        // arrange
        UpdateVocabularyDto updateDto = Mockito.mock(UpdateVocabularyDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .commandValue(updateDto)
                .build();

        Vocabulary existingEntity = Mockito.mock(Vocabulary.class);

        when(vocabularyRepository.findByVocabularyCode(isA(String.class))).thenReturn(List.of(existingEntity));

        // act
        updateVocabularyExecutor.execute(command);

        // assert
        verify(vocabularyMapper, times(1)).updateEntity(updateDto, existingEntity);
        verify(vocabularyRepository, times(1)).update(existingEntity);
    }

    @Test
    void when_noCurrentValidCode_then_IllegalArgumentExceptionWithEntryNotFoundIsThrown() {
        // arrange
        UpdateVocabularyDto updateDto = Mockito.mock(UpdateVocabularyDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "missingVocabulary")
                        .build())
                .commandValue(updateDto)
                .build();

        when(vocabularyRepository.findByVocabularyCode(isA(String.class))).thenReturn(List.of());

        // act and assert
        assertThatThrownBy(() -> updateVocabularyExecutor.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(VOCABULARY_NOT_FOUND);

        verify(vocabularyMapper, never()).updateEntity(Mockito.any(), Mockito.any());
        verify(vocabularyRepository, never()).update(any(Vocabulary.class));
    }

    @Test
    void when_multipleCurrentValidCodes_then_EGovExceptionIsThrown() {
        // arrange
        UpdateVocabularyDto updateDto = Mockito.mock(UpdateVocabularyDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .commandValue(updateDto)
                .build();

        Vocabulary entity1 = Mockito.mock(Vocabulary.class);
        Vocabulary entity2 = Mockito.mock(Vocabulary.class);

        when(vocabularyRepository.findByVocabularyCode(isA(String.class))).thenReturn(List.of(entity1, entity2));

        // act and assert
        assertThrows(EGovException.class,
                () -> updateVocabularyExecutor.execute(command));

        verify(vocabularyMapper, never()).updateEntity(Mockito.any(), Mockito.any());
        verify(vocabularyRepository, never()).update(any(Vocabulary.class));
    }

    @Test
    void when_repositoryThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        UpdateVocabularyDto updateDto = Mockito.mock(UpdateVocabularyDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .commandValue(updateDto)
                .build();

        when(vocabularyRepository.findByVocabularyCode(isA(String.class))).thenThrow(new RuntimeException("db error"));

        // act and assert
        assertThrows(RuntimeException.class, () -> updateVocabularyExecutor.execute(command));
        verifyNoInteractions(vocabularyMapper);
    }

    @Test
    void when_mapperThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        UpdateVocabularyDto updateDto = Mockito.mock(UpdateVocabularyDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .commandValue(updateDto)
                .build();

        Vocabulary existingEntity = Mockito.mock(Vocabulary.class);

        when(vocabularyRepository.findByVocabularyCode(isA(String.class))).thenReturn(List.of(existingEntity));

        doThrow(new RuntimeException("mapping error")).when(vocabularyMapper).updateEntity(updateDto, existingEntity);

        // act and assert
        assertThrows(RuntimeException.class, () -> updateVocabularyExecutor.execute(command));
        verify(vocabularyRepository, never()).update(existingEntity);
    }

    @Test
    void when_vocabularyCodeMissingInCommand_then_NullPointerExceptionIsThrown() {
        // arrange: VOCABULARY_CODE fehlt
        UpdateVocabularyDto updateDto = Mockito.mock(UpdateVocabularyDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder().build())
                .commandValue(updateDto)
                .build();

        // act and assert
        assertThrows(NullPointerException.class, () -> updateVocabularyExecutor.execute(command));
        verifyNoInteractions(vocabularyRepository);
        verifyNoInteractions(vocabularyMapper);
    }

}