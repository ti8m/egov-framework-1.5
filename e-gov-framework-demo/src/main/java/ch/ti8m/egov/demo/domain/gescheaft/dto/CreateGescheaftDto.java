package ch.ti8m.egov.demo.domain.gescheaft.dto;

import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForRoles;
import ch.ti8m.egov.framework.iam.persistence.model.generation.AllowedForStates;
import ch.ti8m.egov.framework.iam.persistence.model.generation.Ruleset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Ruleset(
        code = "CREATE_GESCHEAFT",
        action = "GescheaftApplicationService_CREATE_GESCHEAFT",
        description = "Create Gescheaft",
        category = "Gescheaft"
)
@AllowedForRoles(roles = {"*"})
@AllowedForStates(states = {"*"})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateGescheaftDto {

    private String geschaeftNummer;
    private LocalDate einreichungDatum;
    private LocalDate uebernahmeDatum;
    private UrheberDto urheber;
    private List<Integer> behandelndePersonen;

}
