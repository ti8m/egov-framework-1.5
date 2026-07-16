package ch.ti8m.egov.mdm.api.dto;

import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForRoles;
import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForStates;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Ruleset;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Validation;
import lombok.Builder;

@Ruleset(
        code = "DELETE_VOCABULARY",
        action = "MasterDataApplicationService_DELETE_VOCABULARY",
        description = "Delete Master Data Vocabulary",
        category = "Master Data"
)
@AllowedForRoles(roles = {"MASTER_DATA_ADMIN"})
@AllowedForStates(states = {"*"})
@Builder
@Validation(validation = "['NOT EQUALS', 'root', null]")
public class DeleteVocabularyDto {
    // Nothing to do here. Use this class only to specify the delete-ruleset
}
