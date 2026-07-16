package ch.ti8m.egov.mdm.api.dto;

import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForRoles;
import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForStates;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Ruleset;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Validation;
import ch.ti8m.egov.mdm.persistence.entity.enumerations.NameValidationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Ruleset(
        code = "CREATE_VOCABULARY",
        action = "MasterDataApplicationService_CREATE_VOCABULARY",
        description = "Create Master Data Vocabulary",
        category = "Master Data"
)
@AllowedForRoles(roles = {"MASTER_DATA_ADMIN"})
@AllowedForStates(states = {"*"})
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Validation(validation = "['NOT EQUALS', 'root', null]")
public class CreateVocabularyDto {

    private String code;
    private boolean modifiable;
    private boolean sortable;
    private NameValidationType nameValidationType;
    private List<FieldDefinitionDto> fields;
    private List<LanguageDefinitionDto> languages;
    private List<VocabularyLnDto> localizations;

}
