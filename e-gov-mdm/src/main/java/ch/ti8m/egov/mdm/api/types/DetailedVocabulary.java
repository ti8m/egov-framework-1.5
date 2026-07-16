package ch.ti8m.egov.mdm.api.types;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DetailedVocabulary {

    private final List<String> languages;
    private final List<Field> fields;
    private final List<SimplifiedCode> codes;
    private final long vocabularyId;
    private final String shortName;
    private final String longName;

}
