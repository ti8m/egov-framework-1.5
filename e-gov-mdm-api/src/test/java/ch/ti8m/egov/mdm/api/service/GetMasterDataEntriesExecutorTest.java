package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntriesDto;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.mapper.MasterDataEntryMapper;
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
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VALID_AT;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.WITH_HISTORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetMasterDataEntriesExecutorTest {

    @Mock
    private MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    @Mock
    private MasterDataEntryMapper masterDataEntryMapper;

    @InjectMocks
    private GetMasterDataEntriesExecutor getMasterDataEntriesExecutor;

    @Test
    void when_findByVocabularyCodeReturnsEmptyList_then_executeReturnsDtoWithEmptyRecords() {
        // arrange
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        when(masterDataGenericEntityRepository.findValidByVocabularyCode(
                isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        when(masterDataEntryMapper.toListOfGetMasterDataEntry(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        // act
        GetMasterDataEntriesDto result = getMasterDataEntriesExecutor.execute(command);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getEntries()).isNotNull().isEmpty();
        Mockito.verify(masterDataGenericEntityRepository, times(1))
                .findValidByVocabularyCode(isA(String.class), isA(LocalDateTime.class));
        Mockito.verify(masterDataEntryMapper, times(1))
                .toListOfGetMasterDataEntry(Collections.emptyList());
    }

    @Test
    void when_repositoryThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        when(masterDataGenericEntityRepository.findValidByVocabularyCode(
                isA(String.class), isA(LocalDateTime.class)))
                .thenThrow(new RuntimeException("db error"));

        // act and assert
        assertThrows(RuntimeException.class,
                () -> getMasterDataEntriesExecutor.execute(command));
        // Mapper darf in diesem Fall nicht aufgerufen werden
        verifyNoInteractions(masterDataEntryMapper);
    }

    @Test
    void when_mapperThrowsRuntimeException_then_executePropagatesException() {
        // arrange
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        MasterDataGenericEntity masterDataGenericEntity = Mockito.mock(MasterDataGenericEntity.class);
        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                isA(String.class), isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(List.of(masterDataGenericEntity));

        when(masterDataEntryMapper.toListOfGetMasterDataEntry(List.of(masterDataGenericEntity)))
                .thenThrow(new RuntimeException("mapping error"));

        // act and assert
        assertThrows(RuntimeException.class,
                () -> getMasterDataEntriesExecutor.execute(command));
    }

    @Test
    void when_vocabularyCodeMissingInCommand_then_exceptionIsThrown() {
        // arrange: Command ohne VOCABULARY_CODE
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        // act and assert
        assertThrows(NullPointerException.class,
                () -> getMasterDataEntriesExecutor.execute(command));

        // Repository und Mapper sollten in diesem Fall nicht aufgerufen werden
        verifyNoInteractions(masterDataGenericEntityRepository);
        verifyNoInteractions(masterDataEntryMapper);
    }

    @Test
    void when_withHistoryFalseAndSingleResult_then_executeReturnsMappedDto() {
        // arrange
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .add(VALID_AT, validityDate)
                        .add(WITH_HISTORY, Boolean.FALSE)
                        .build())
                .build();

        MasterDataGenericEntity entity = createCurrent(validityDate);
        List<MasterDataGenericEntity> entities = List.of(entity);
        when(masterDataGenericEntityRepository.findValidByVocabularyCode(
                isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(entities);

        GetMasterDataEntryDto mappedDto = createCurrentDto(validityDate);
        List<GetMasterDataEntryDto> mappedDTOs = List.of(mappedDto);
        when(masterDataEntryMapper.toListOfGetMasterDataEntry(entities))
                .thenReturn(mappedDTOs);

        // act
        GetMasterDataEntriesDto result = getMasterDataEntriesExecutor.execute(command);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getEntries()).isNotNull().hasSize(1).containsExactly(mappedDto);
        Mockito.verify(masterDataGenericEntityRepository, times(1))
                .findValidByVocabularyCode(isA(String.class), isA(LocalDateTime.class));
        Mockito.verify(masterDataEntryMapper, times(1))
                .toListOfGetMasterDataEntry(entities);
    }

    @Test
    void when_withHistoryMissingDefaultsToFalse_then_findByVocabularyCodeIsUsed() {
        // arrange: WITH_HISTORY nicht gesetzt -> Default FALSE
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .add(VALID_AT, validityDate)
                        .build())
                .build();

        MasterDataGenericEntity entity = createSecond(validityDate);
        List<MasterDataGenericEntity> entities = List.of(entity);
        when(masterDataGenericEntityRepository.findValidByVocabularyCode(
                isA(String.class), isA(LocalDateTime.class)))
                .thenReturn(entities);

        GetMasterDataEntryDto mappedDto = createSecondDto(validityDate);
        List<GetMasterDataEntryDto> mappedDTOs = List.of(mappedDto);
        when(masterDataEntryMapper.toListOfGetMasterDataEntry(entities))
                .thenReturn(mappedDTOs);

        // act
        GetMasterDataEntriesDto result = getMasterDataEntriesExecutor.execute(command);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getEntries()).isNotNull().hasSize(1).containsExactly(mappedDto);
        Mockito.verify(masterDataGenericEntityRepository, times(1))
                .findValidByVocabularyCode(isA(String.class), isA(LocalDateTime.class));
        Mockito.verify(masterDataEntryMapper, times(1))
                .toListOfGetMasterDataEntry(entities);
    }

    @Test
    void when_withHistoryTrue_then_findValidByVocabularyCodeAndEntryCodeWithHistoryIsUsed() {
        // arrange
        final LocalDateTime validityDate = LocalDateTime.now();
        final Command command = Command.builder()
                .parameters(Parameters.builder()
                        .add(VOCABULARY_CODE, "testVocabulary")
                        .add(ENTRY_CODE, "entry01")
                        .add(VALID_AT, validityDate)
                        .add(WITH_HISTORY, Boolean.TRUE)
                        .build())
                .build();

        List<MasterDataGenericEntity> currentWithHistory = getOneEntryWithHistoricValue(validityDate);
        when(masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCodeWithHistory(
                isA(String.class), isA(String.class)))
                .thenReturn(currentWithHistory);

        List<GetMasterDataEntryDto> mappedDTOs = getOneEntryDtoWithHistoricValue(validityDate);
        when(masterDataEntryMapper.toListOfGetMasterDataEntry(currentWithHistory))
                .thenReturn(mappedDTOs);

        // act
        GetMasterDataEntriesDto result = getMasterDataEntriesExecutor.execute(command);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getEntries()).isNotNull().hasSize(2);
        Mockito.verify(masterDataGenericEntityRepository, times(1))
                .findValidByVocabularyCodeAndEntryCodeWithHistory(isA(String.class), isA(String.class));
        Mockito.verify(masterDataEntryMapper, times(1))
                .toListOfGetMasterDataEntry(currentWithHistory);
    }

    private List<MasterDataGenericEntity> getOneEntryWithHistoricValue(LocalDateTime validityDate) {
        MasterDataGenericEntity current = createCurrent(validityDate);
        MasterDataGenericEntity historic = createHistoric(validityDate);
        return List.of(current, historic);
    }

    private MasterDataGenericEntity createCurrent(LocalDateTime validityDate) {
        return MasterDataGenericEntity.builder()
                .id(1L)
                .validFrom(validityDate.minusDays(1))
                .validTo(validityDate.plusDays(1))
                .code("entry01")
                .vocabularyCode("testVocabulary")
                .build();
    }

    private MasterDataGenericEntity createHistoric(LocalDateTime validityDate) {
        return MasterDataGenericEntity.builder()
                .id(2L)
                .validFrom(validityDate.minusDays(2))
                .validTo(validityDate.minusDays(1))
                .code("entry01")
                .vocabularyCode("testVocabulary")
                .build();
    }

    private MasterDataGenericEntity createSecond(LocalDateTime validityDate) {
        return MasterDataGenericEntity.builder()
                .id(3L)
                .validFrom(validityDate.minusDays(1))
                .validTo(validityDate.plusDays(1))
                .code("entry02")
                .vocabularyCode("testVocabulary")
                .build();
    }

    private List<GetMasterDataEntryDto> getOneEntryDtoWithHistoricValue(LocalDateTime validityDate) {
        GetMasterDataEntryDto currentDto = createCurrentDto(validityDate);
        GetMasterDataEntryDto historicDto = createHistoricDto(validityDate);
        return List.of(currentDto, historicDto);
    }

    private GetMasterDataEntryDto createCurrentDto(LocalDateTime validityDate) {
        return GetMasterDataEntryDto.builder()
                .validFrom(validityDate.minusDays(1))
                .validTo(validityDate.plusDays(1))
                .code("entry01")
                .vocabularyCode("testVocabulary")
                .build();
    }

    private GetMasterDataEntryDto createHistoricDto(LocalDateTime validityDate) {
        return GetMasterDataEntryDto.builder()
                .validFrom(validityDate.minusDays(2))
                .validTo(validityDate.minusDays(1))
                .code("entry01")
                .vocabularyCode("testVocabulary")
                .build();
    }

    private GetMasterDataEntryDto createSecondDto(LocalDateTime validityDate) {
        return GetMasterDataEntryDto.builder()
                .validFrom(validityDate.minusDays(1))
                .validTo(validityDate.plusDays(1))
                .code("entry02")
                .vocabularyCode("testVocabulary")
                .build();
    }

}