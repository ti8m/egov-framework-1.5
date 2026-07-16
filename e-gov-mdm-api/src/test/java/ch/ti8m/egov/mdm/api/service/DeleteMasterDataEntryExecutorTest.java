package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.ENTRY_CODE;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteMasterDataEntryExecutorTest {

    @Mock
    private MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    @InjectMocks
    private DeleteMasterDataEntryExecutor deleteMasterDataEntryExecutor;

    @Test
    void when_findByVocabularyCodeReturnsOneEntry_then_deleteGetsCalled() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "testEntry")
                        .build())
                .build();
        MasterDataGenericEntity masterDataGenericEntity = Mockito.mock(MasterDataGenericEntity.class);
        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class))).thenReturn(List.of(masterDataGenericEntity));
        // act
        deleteMasterDataEntryExecutor.execute(command);
        // assert
        Mockito.verify(masterDataGenericEntityRepository, Mockito.times(1)).delete(masterDataGenericEntity);
    }

    @Test
    void when_findByVocabularyCodeReturnsTwoEntries_then_EGovExceptionIsThrown() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "testEntry")
                        .build())
                .build();
        MasterDataGenericEntity masterDataGenericEntityOne = Mockito.mock(MasterDataGenericEntity.class);
        MasterDataGenericEntity masterDataGenericEntityTwo = Mockito.mock(MasterDataGenericEntity.class);
        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(List.of(masterDataGenericEntityOne, masterDataGenericEntityTwo));
        // act and assert
        assertThrows(EGovException.class, () -> deleteMasterDataEntryExecutor.execute(command));
    }

    @Test
    void when_findByVocabularyCodeReturnsNoEntries_then_IllegalArgumentExceptionIsThrown() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "testEntry")
                        .build())
                .build();
        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        // act
        deleteMasterDataEntryExecutor.execute(command);
        // assert
        verify(masterDataGenericEntityRepository, Mockito.times(0)).delete(any(MasterDataGenericEntity.class));
    }

}
