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
        code = "GET_VOCABULARIES",
        action = "MasterDataApplicationService_GET_VOCABULARIES",
        description = "Get Master Data Vocabularies",
        category = "Master Data"
)
@AllowedForRoles(roles = {"*"})
@AllowedForStates(states = {"*"})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetVocabulariesDto {

    private List<GetVocabularyDto> vocabularies;

}
