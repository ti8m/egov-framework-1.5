package ch.ti8m.egov.mdm.api.dto;

import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForRoles;
import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForStates;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Ruleset;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Validation;

@Ruleset(
        code = "DELETE_MASTER_DATA_ENTRY",
        action = "MasterDataApplicationService_DELETE_MASTER_DATA_ENTRY",
        description = "Delete Master Data Entry",
        category = "Master Data"
)
@AllowedForRoles(roles = {"MASTER_DATA_ADMIN"})
@AllowedForStates(states = {"*"})
@Validation(validation = "['NOT EQUALS', 'root', null]")
public class DeleteMasterDataEntryDto {
    // Nothing to do here. Use this class only to specify the delete-ruleset
}
