package ch.ti8m.egov.mdm.api.dto;

import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForRoles;
import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForStates;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Ruleset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Ruleset(
        code = "GET_VALID_MASTER_DATA_ENTRIES",
        action = "MasterDataApplicationService_GET_VALID_MASTER_DATA_ENTRIES",
        description = "Get Master Data Entries",
        category = "Master Data"
)
@AllowedForRoles(roles = {"*"})
@AllowedForStates(states = {"*"})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetMasterDataEntriesDto {
    private List<GetMasterDataEntryDto> entries;

}
