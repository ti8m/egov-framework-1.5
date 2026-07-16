package ch.ti8m.egov.mdm.api.types;

import ch.ti8m.egov.mdm.persistence.entity.FieldDefinition;
import ch.ti8m.egov.mdm.persistence.entity.FieldDefinitionLn;
import lombok.Builder;
import lombok.Getter;

import java.util.Locale;

@Builder
@Getter
public class Field {

    private final String machineName;
    private final String type;
    private final String label;
    private final boolean checkboxDefault;
    private final long parentVocabulary;

    public static Field fromField(final FieldDefinition field, final Locale userLanguage) {
        return Field.builder()
                .machineName(field.getCode())
                .type(field.getType())
                .label(field.getLocalization(userLanguage.getLanguage()).map(FieldDefinitionLn::getName).orElse(null))
                .checkboxDefault(false)
                .parentVocabulary(field.getDefaultValue() == null ? -1 : Long.parseLong(field.getDefaultValue()))
                .build();
    }
}
