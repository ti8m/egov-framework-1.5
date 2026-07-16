package ch.ti8m.egov.mdm.api.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermTranslation {
    private String shortName;
    private String longName;
}
