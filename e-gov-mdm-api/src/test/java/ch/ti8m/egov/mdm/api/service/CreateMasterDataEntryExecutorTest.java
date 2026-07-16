package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.dto.CreateMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.mapper.MasterDataEntryMapper;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
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
class CreateMasterDataEntryExecutorTest {

    @Mock
    private MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    @Mock
    private MasterDataEntryMapper masterDataEntryMapper;

    @InjectMocks
    private CreateMasterDataEntryExecutor createMasterDataEntryExecutor;

    @Test
    void when_mapperAndRepositoryMockedCorrectly_then_executeReturnsIdAndSaveGetsCalled() {
        // arrange
        final CreateMasterDataEntryDto createMasterDataEntryDto = Mockito.mock(CreateMasterDataEntryDto.class);
        final Command command = Command.builder()
                .commandValue(createMasterDataEntryDto)
                .build();
        final MasterDataGenericEntity masterDataGenericEntity = Mockito.mock(MasterDataGenericEntity.class);
        when(masterDataEntryMapper.toEntity(createMasterDataEntryDto)).thenReturn(masterDataGenericEntity);
        Long ID = 1L;
        when(masterDataGenericEntity.getId()).thenReturn(ID);
        doNothing().when(masterDataGenericEntityRepository).save(isA(MasterDataGenericEntity.class));
        // act
        Long id = createMasterDataEntryExecutor.execute(command);
        // assert
        assertThat(id).isNotNull().isEqualTo(ID);
        verify(masterDataGenericEntityRepository, times(1)).save(masterDataGenericEntity);
    }

    @Test
    void when_mapperThrowsARuntimeException_then_saveHasNoInteraction() {
        // arrange
        final CreateMasterDataEntryDto createMasterDataEntryDto = Mockito.mock(CreateMasterDataEntryDto.class);
        final Command command = Command.builder()
                .commandValue(createMasterDataEntryDto)
                .build();
        final MasterDataGenericEntity masterDataGenericEntity = Mockito.mock(MasterDataGenericEntity.class);
        when(masterDataEntryMapper.toEntity(createMasterDataEntryDto)).thenThrow(new RuntimeException("RuntimeException in mapper"));
        // act and assert
        assertThrows(RuntimeException.class, () -> createMasterDataEntryExecutor.execute(command));
        verify(masterDataGenericEntityRepository, times(0)).save(masterDataGenericEntity);
    }

    @Test
    void when_repositoryThrowsARuntimeException_then_getIdHasNoInteraction() {
        // arrange
        final CreateMasterDataEntryDto createMasterDataEntryDto = Mockito.mock(CreateMasterDataEntryDto.class);
        final Command command = Command.builder()
                .commandValue(createMasterDataEntryDto)
                .build();
        final MasterDataGenericEntity masterDataGenericEntity = Mockito.mock(MasterDataGenericEntity.class);
        when(masterDataEntryMapper.toEntity(createMasterDataEntryDto)).thenReturn(masterDataGenericEntity);
        doThrow(RuntimeException.class).when(masterDataGenericEntityRepository)
                .save(isA(MasterDataGenericEntity.class));
        // act and assert
        assertThrows(RuntimeException.class, () -> createMasterDataEntryExecutor.execute(command));
        verify(masterDataGenericEntity, times(0)).getId();
    }

}