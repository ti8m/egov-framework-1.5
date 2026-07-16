package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntryDto;
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
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VALID_AT;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMasterDataEntryExecutorTest {

    @Mock
    private MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    @Mock
    private MasterDataEntryMapper masterDataEntryMapper;

    @InjectMocks
    private GetMasterDataEntryExecutor getMasterDataEntryExecutor;

    @Test
    void when_repositoryReturnsEmptyList_then_executeReturnsNull() {
        // arrange
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(List.of());

        // act
        GetMasterDataEntryDto result = getMasterDataEntryExecutor.execute(command);

        // assert
        assertThat(result).isNull();
        // Mapper darf hier nicht aufgerufen werden
        verifyNoInteractions(masterDataEntryMapper);
    }

    @Test
    void when_repositoryReturnsMoreThanOneResult_then_EGovExceptionIsThrown() {
        // arrange
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        MasterDataGenericEntity entity1 = Mockito.mock(MasterDataGenericEntity.class);
        MasterDataGenericEntity entity2 = Mockito.mock(MasterDataGenericEntity.class);

        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(List.of(entity1, entity2));

        // act and assert
        assertThrows(EGovException.class,
                () -> getMasterDataEntryExecutor.execute(command));

        // Mapper darf in diesem Fall nicht aufgerufen werden
        verifyNoInteractions(masterDataEntryMapper);
    }

    @Test
    void when_repositoryThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenThrow(new RuntimeException("db error"));

        // act and assert
        assertThrows(RuntimeException.class,
                () -> getMasterDataEntryExecutor.execute(command));

        verifyNoInteractions(masterDataEntryMapper);
    }

    @Test
    void when_mapperThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        MasterDataGenericEntity entity = Mockito.mock(MasterDataGenericEntity.class);
        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(List.of(entity));

        when(masterDataEntryMapper.toGetMasterDataEntry(entity))
                .thenThrow(new RuntimeException("mapping error"));

        // act and assert
        assertThrows(RuntimeException.class,
                () -> getMasterDataEntryExecutor.execute(command));
    }

    @Test
    void when_vocabularyCodeMissingInCommand_then_NullPointerExceptionIsThrown() {
        // arrange: Command ohne VOCABULARY_CODE
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(ENTRY_CODE, "entry01")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        // act and assert
        assertThrows(NullPointerException.class,
                () -> getMasterDataEntryExecutor.execute(command));

        verifyNoInteractions(masterDataGenericEntityRepository);
        verifyNoInteractions(masterDataEntryMapper);
    }

    @Test
    void when_entryCodeMissingInCommand_then_NullPointerExceptionIsThrown() {
        // arrange: Command ohne ENTRY_CODE
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        // act and assert
        assertThrows(NullPointerException.class,
                () -> getMasterDataEntryExecutor.execute(command));

        verifyNoInteractions(masterDataGenericEntityRepository);
        verifyNoInteractions(masterDataEntryMapper);
    }

}