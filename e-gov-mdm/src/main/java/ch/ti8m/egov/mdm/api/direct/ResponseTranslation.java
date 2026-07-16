package ch.ti8m.egov.mdm.api.direct;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ResponseTranslation {

    private String shortName;
    private String longName;

}
