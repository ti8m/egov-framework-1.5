package ch.ti8m.egov.mdm.api.dto;

import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForRoles;
import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForStates;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Ruleset;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Validation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Ruleset(
        code = "CREATE_MASTER_DATA_ENTRY",
        action = "MasterDataApplicationService_CREATE_MASTER_DATA_ENTRY",
        description = "Create Master Data Entry",
        category = "Master Data"
)
@AllowedForRoles(roles = {"MASTER_DATA_ADMIN"})
@AllowedForStates(states = {"*"})
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Validation(validation = "['NOT EQUALS', 'root', null]")
public class CreateMasterDataEntryDto {

    private String code;
    private String vocabularyCode;
    private String languageCode;
    private int weight;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String shortName;
    private String longName;
    private Map<String, Object> additionalContent;

}
