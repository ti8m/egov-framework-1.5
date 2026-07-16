package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.dto.UpdateMasterDataEntryDto;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.mdm.api.mapper.MasterDataEntryMapper;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.ENTRY_CODE;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static ch.ti8m.egov.mdm.api.service.ResponseMessages.ENTRY_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateMasterDataEntryExecutorTest {

    @Mock
    private MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    @Mock
    private MasterDataEntryMapper masterDataEntryMapper;

    @InjectMocks
    private UpdateMasterDataEntryExecutor updateMasterDataEntryExecutor;

    @Test
    void when_singleCurrentValidCode_then_entityIsUpdatedAndSaved() {
        // arrange
        UpdateMasterDataEntryDto updateDto = Mockito.mock(UpdateMasterDataEntryDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .build())
                .commandValue(updateDto)
                .build();

        MasterDataGenericEntity existingEntity = Mockito.mock(MasterDataGenericEntity.class);

        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(List.of(existingEntity));

        // act
        updateMasterDataEntryExecutor.execute(command);

        // assert
        Mockito.verify(masterDataEntryMapper, times(1))
                .updateEntity(updateDto, existingEntity);
        Mockito.verify(masterDataGenericEntityRepository, times(1))
                .update(existingEntity);
    }

    @Test
    void when_noCurrentValidCode_then_IllegalArgumentExceptionWithEntryNotFoundIsThrown() {
        // arrange
        UpdateMasterDataEntryDto updateDto = Mockito.mock(UpdateMasterDataEntryDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "missingEntry")
                        .build())
                .commandValue(updateDto)
                .build();

        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(List.of());

        // act and assert
        assertThatThrownBy(() -> updateMasterDataEntryExecutor.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ENTRY_NOT_FOUND);

        Mockito.verify(masterDataEntryMapper, never())
                .updateEntity(Mockito.any(), Mockito.any());
        Mockito.verify(masterDataGenericEntityRepository, never())
                .update(any(MasterDataGenericEntity.class));
    }

    @Test
    void when_multipleCurrentValidCodes_then_EGovExceptionIsThrown() {
        // arrange
        UpdateMasterDataEntryDto updateDto = Mockito.mock(UpdateMasterDataEntryDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .build())
                .commandValue(updateDto)
                .build();

        MasterDataGenericEntity entity1 = Mockito.mock(MasterDataGenericEntity.class);
        MasterDataGenericEntity entity2 = Mockito.mock(MasterDataGenericEntity.class);

        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(List.of(entity1, entity2));

        // act and assert
        assertThrows(EGovException.class,
                () -> updateMasterDataEntryExecutor.execute(command));

        Mockito.verify(masterDataEntryMapper, never())
                .updateEntity(Mockito.any(), Mockito.any());
        Mockito.verify(masterDataGenericEntityRepository, never())
                .update(any(MasterDataGenericEntity.class));
    }

    @Test
    void when_repositoryThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        UpdateMasterDataEntryDto updateDto = Mockito.mock(UpdateMasterDataEntryDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .build())
                .commandValue(updateDto)
                .build();

        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenThrow(new RuntimeException("db error"));

        // act and assert
        assertThrows(RuntimeException.class,
                () -> updateMasterDataEntryExecutor.execute(command));

        verifyNoInteractions(masterDataEntryMapper);
    }

    @Test
    void when_mapperThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        UpdateMasterDataEntryDto updateDto = Mockito.mock(UpdateMasterDataEntryDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .build())
                .commandValue(updateDto)
                .build();

        MasterDataGenericEntity existingEntity = Mockito.mock(MasterDataGenericEntity.class);

        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(List.of(existingEntity));

        Mockito.doThrow(new RuntimeException("mapping error"))
                .when(masterDataEntryMapper)
                .updateEntity(updateDto, existingEntity);

        // act and assert
        assertThrows(RuntimeException.class,
                () -> updateMasterDataEntryExecutor.execute(command));

        Mockito.verify(masterDataGenericEntityRepository, never())
                .update(existingEntity);
    }

    @Test
    void when_vocabularyCodeMissingInCommand_then_NullPointerExceptionIsThrown() {
        // arrange: VOCABULARY_CODE fehlt
        UpdateMasterDataEntryDto updateDto = Mockito.mock(UpdateMasterDataEntryDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(ENTRY_CODE, "entry01")
                        .build())
                .commandValue(updateDto)
                .build();

        // act and assert
        assertThrows(NullPointerException.class,
                () -> updateMasterDataEntryExecutor.execute(command));

        verifyNoInteractions(masterDataGenericEntityRepository);
        verifyNoInteractions(masterDataEntryMapper);
    }

    @Test
    void when_entryCodeMissingInCommand_then_NullPointerExceptionIsThrown() {
        // arrange: ENTRY_CODE fehlt
        UpdateMasterDataEntryDto updateDto = Mockito.mock(UpdateMasterDataEntryDto.class);

        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .commandValue(updateDto)
                .build();

        // act and assert
        assertThrows(NullPointerException.class,
                () -> updateMasterDataEntryExecutor.execute(command));

        verifyNoInteractions(masterDataGenericEntityRepository);
        verifyNoInteractions(masterDataEntryMapper);
    }

}