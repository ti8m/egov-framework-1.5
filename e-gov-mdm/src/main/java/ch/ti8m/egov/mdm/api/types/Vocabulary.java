package ch.ti8m.egov.mdm.api.types;

import ch.ti8m.egov.mdm.persistence.entity.VocabularyLn;
import lombok.Builder;
import lombok.Getter;

import java.util.Locale;

@Builder
@Getter
public class Vocabulary {

    private final long vocabularyId;
    private final boolean modifiable;
    private final boolean sortable;
    private final String shortName;
    private final String longName;

    public static Vocabulary fromVocabulary(ch.ti8m.egov.mdm.persistence.entity.Vocabulary vocabulary, Locale userLanguage) {
        return vocabulary.getLocalization(userLanguage.getLanguage())
                .map(vocabularyLn -> of(vocabulary, vocabularyLn))
                .orElseThrow();
    }

    private static Vocabulary of(ch.ti8m.egov.mdm.persistence.entity.Vocabulary vocabulary, VocabularyLn vocabularyLn) {
        return Vocabulary.builder()
                .vocabularyId(vocabulary.getId())
                .modifiable(vocabulary.isModifiable())
                .sortable(vocabulary.isSortable())
                .shortName(vocabularyLn.getName())
                .longName(vocabularyLn.getDescription())
                .build();
    }
}
