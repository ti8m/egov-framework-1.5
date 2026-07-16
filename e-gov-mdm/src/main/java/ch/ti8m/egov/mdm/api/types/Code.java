package ch.ti8m.egov.mdm.api.types;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@NoArgsConstructor
@Data
public class Code {
    private LocalDate validTo;
    private Map<String, String> fields;
    private Map<String, TermTranslation> currentValue;
    private Map<String, TermTranslation> futureValue;
    private long mvccVersion;
}
