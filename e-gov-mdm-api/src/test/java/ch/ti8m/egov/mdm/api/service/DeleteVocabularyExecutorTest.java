package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteVocabularyExecutorTest {

    @Mock
    private VocabularyRepository vocabularyRepository;

    @InjectMocks
    private DeleteVocabularyExecutor deleteVocabularyExecutor;

    @Test
    void when_findByVocabularyCodeReturnsOneEntry_then_deleteGetsCalled() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .build();
        Vocabulary vocabulary = Mockito.mock(Vocabulary.class);
        when(vocabularyRepository.findBy(eq(Vocabulary.Fields.code), isA(String.class))).thenReturn(List.of(vocabulary));
        // act
        deleteVocabularyExecutor.execute(command);
        // assert
        verify(vocabularyRepository, Mockito.times(1)).delete(vocabulary);
    }

    @Test
    void when_findByVocabularyCodeReturnsTwoEntries_then_EGovExceptionIsThrown() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .build();
        Vocabulary vocabularyOne = Mockito.mock(Vocabulary.class);
        Vocabulary vocabularyTwo = Mockito.mock(Vocabulary.class);
        when(vocabularyRepository.findBy(eq(Vocabulary.Fields.code), isA(String.class))).thenReturn(List.of(vocabularyOne, vocabularyTwo));
        // act and assert
        assertThrows(EGovException.class, () -> deleteVocabularyExecutor.execute(command));
    }

    @Test
    void when_findByVocabularyCodeReturnsNoEntries_then_deleteOfVocabularyRepositoryIsNotCalled() {
        // arrange
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .build())
                .build();
        when(vocabularyRepository.findBy(eq(Vocabulary.Fields.code), isA(String.class))).thenReturn(Collections.emptyList());
        // act
        deleteVocabularyExecutor.execute(command);

        // assert
        verify(vocabularyRepository, Mockito.times(0)).delete(any(Vocabulary.class));
    }

}
