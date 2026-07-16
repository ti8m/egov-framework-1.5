package ch.ti8m.egov.mdm.api.dto;

import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForRoles;
import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForStates;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Ruleset;
import ch.ti8m.egov.mdm.persistence.entity.enumerations.NameValidationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Ruleset(
        code = "GET_VOCABULARY",
        action = "MasterDataApplicationService_GET_VOCABULARY",
        description = "Get Master Data Vocabulary",
        category = "Master Data"
)
@AllowedForRoles(roles = {"*"})
@AllowedForStates(states = {"*"})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetVocabularyDto {

    private String code;
    private boolean modifiable;
    private boolean sortable;
    private NameValidationType nameValidationType;
    private List<FieldDefinitionDto> fields;
    private List<LanguageDefinitionDto> languages;
    private List<VocabularyLnDto> localizations;

}
