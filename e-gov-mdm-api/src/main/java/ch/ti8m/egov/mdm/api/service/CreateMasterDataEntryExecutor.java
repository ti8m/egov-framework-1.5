package ch.ti8m.egov.mdm.api.service;

import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.framework.validation.command.Executor;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.mapper.MasterDataEntryMapper;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateMasterDataEntryExecutor implements Executor {

    @PrimaryRepository
    private final MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    private final MasterDataEntryMapper masterDataEntryMapper;

    @Override
    public Long execute(final Command command) {
        final MasterDataGenericEntity masterDataGenericEntity = masterDataEntryMapper.toEntity(command.unwrap());
        masterDataGenericEntityRepository.save(masterDataGenericEntity);
        return masterDataGenericEntity.getId();
    }

}
