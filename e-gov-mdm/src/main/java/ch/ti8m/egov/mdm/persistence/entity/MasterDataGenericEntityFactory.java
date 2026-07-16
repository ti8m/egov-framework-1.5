package ch.ti8m.egov.mdm.persistence.entity;

import ch.ti8m.egov.mdm.api.types.DetailedCode;
import ch.ti8m.egov.mdm.api.types.TermTranslation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MasterDataGenericEntityFactory {
    public static MasterDataGenericEntity fromDetailedCode(final String vocabularyCode, final DetailedCode detailedCode, final String language, final TermTranslation termTranslation, final LocalDateTime validFrom, final LocalDateTime validTo) {
        Map<String, Object> additionalContent = detailedCode.getFields().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new MasterDataGenericEntity(vocabularyCode, detailedCode.getCode(), language, termTranslation.getShortName(), termTranslation.getLongName(), validFrom, validTo, additionalContent);
    }

    public static MasterDataGenericEntity fromCode(final String vocabularyCode, final String code, final Map<String, String> fields, final String language, final TermTranslation termTranslation, final LocalDateTime validFrom, final LocalDateTime validTo) {
        Map<String, Object> additionalContent = fields.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new MasterDataGenericEntity(vocabularyCode, code, language, termTranslation.getShortName(), termTranslation.getLongName(), validFrom, validTo, additionalContent);
    }
}
