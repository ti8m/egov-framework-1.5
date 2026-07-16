package ch.ti8m.egov.mdm.api.dto;

import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForRoles;
import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForStates;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Ruleset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Ruleset(
        code = "GET_VALID_MASTER_DATA_ENTRY",
        action = "MasterDataApplicationService_GET_VALID_MASTER_DATA_ENTRY",
        description = "Get Master Data Entry",
        category = "Master Data"
)
@AllowedForRoles(roles = {"*"})
@AllowedForStates(states = {"*"})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetMasterDataEntryDto {

    private String code;
    private String vocabularyCode;
    private String languageCode;
    private int weight;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String shortName;
    private String longName;

}
