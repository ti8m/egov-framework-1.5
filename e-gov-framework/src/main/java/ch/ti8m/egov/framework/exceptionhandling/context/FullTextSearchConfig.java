package ch.ti8m.egov.framework.exceptionhandling.context;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
public final class FullTextSearchConfig {

    public static final String CURRENT_DATE_PLACEHOLDER = "$CURRENT_DATE_PLACEHOLDER$";
    public static final String TIME_RELEVANCE_MULTIPLIER = "(0.5 + 0.5 / (DATEDIFF(YEAR, SearchTable.%s, " + FullTextSearchConfig.CURRENT_DATE_PLACEHOLDER + ") + 1))";

    private final Map<String, Integer> fields = new HashMap<>();
    private String fieldNameTimeRelevance = "";

    private static void throwIfInvalidFieldname(final String fieldName) {
        if (!StringUtils.isAlpha(fieldName)) {
            throw new EGovException(ExceptionCode.INVALID_DATA,
                    "FieldName is invalid",
                    Map.of("name", fieldName == null ? "null" : fieldName));
        }
    }

    /**
     * Fügt ein Feld für die Volltextsuche hinzu.
     * <p>
     * <b>Wichtig</b>: Die Feldkonfigurationen werden nach jeder Abfrage im Framework zurückgesetzt. Daher
     * sollte diese Methode unmittelbar vor jeder Suchabfrage aufgerufen werden, um die
     * gewünschten Felder korrekt zu setzen.
     *
     * @param fieldName    Der Name des Felds für die Suche. Muss aus alphabetischen Zeichen bestehen.
     * @param searchWeight Die Gewichtung des Felds für die Suche.
     */
    public void addField(final String fieldName, final Integer searchWeight) {
        FullTextSearchConfig.throwIfInvalidFieldname(fieldName);

        fields.put(fieldName, searchWeight);
    }

    /**
     * Mit dieser Methode kann der Volltextsuche ein DATETIME-Feld der Datenbank mitgegeben werden. Wird dies gemacht,
     * fliesst in die Gewichtung auch die Aktualität der Ergebnisse mit ein. D.h. neuere Ergebnisse erscheinen
     * tendenziell weiter oben.
     *
     * @param fieldNameTimeRelevance Der Name des Felds für die Suche. Muss aus alphabetischen Zeichen bestehen.
     */
    public void setFieldNameTimeRelevance(final String fieldNameTimeRelevance) {
        this.fieldNameTimeRelevance = fieldNameTimeRelevance;
    }

    public String getTimeRelevanceMultiplier() {
        if (isTimeRelevanceActive()) {
            return String.format(FullTextSearchConfig.TIME_RELEVANCE_MULTIPLIER, getFieldNameTimeRelevance());
        }
        return "1";
    }

    public void clear() {
        fields.clear();
        fieldNameTimeRelevance = null;
    }

    public boolean isFullTextSearchActive() {
        return !fields.isEmpty();
    }

    public boolean isTimeRelevanceActive() {
        return !fieldNameTimeRelevance.isBlank();
    }
}
