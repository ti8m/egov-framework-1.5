package ch.ti8m.egov.airulegeneration.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RuleGenerator {

    private final AzureOpenAiChatModel chatModel;
    private final RuleTranslator ruleTranslator;

    public List<Object> generateRule(final String className, final String ruleDescription) {
        final String prompt = """
                Du bist ein Übersetzer von natürlicher Sprache in formale Validierungsregeln. Du hältst dich exakt an die Spezifikation die weiter unten gegeben ist.
                
                Übersetze folgende Regelbeschreibung im untenstehenden Kontext für das unten stehende Objekt:
                Regelbeschreibung:
                ```
                %s
                ```
                Objekt:
                ```
                %s
                ```
                
                Die zur Verfügung stehenden Regeln sind folgende:
                ```
                NOT negiert die folgende Funktion.
                EQUAL erwartet beliebig viele Parameter. Es gibt TRUE zurück, wenn alle Parameter gleich sind.
                AND erwartet beliebig viele Parameter. Es gibt TRUE zurück, wenn alle Parameter TRUE sind.
                OR erwartet beliebig viele Parameter. Es gibt TRUE zurück, wenn einer der Parameter TRUE ist. Es handelt sich somit um ein inklusives OR. Beachte, dass es kein 'entweder oder' ist.
                LESS_THAN erwartet zwei Parameter. Diese Parameter können Zahlen oder Zeiten oder Daten oder Daten mit Zeiten sein. Es gibt TRUE zurück, wenn der erste Parameter kleiner ist als der zweiter Parameter oder der erste Parameter zeitlich vor dem zweiten Parameter liegt.
                LESS_EQUAL_THAN erwartet zwei Parameter. Diese Parameter können Zahlen oder Zeiten oder Daten oder Daten mit Zeiten sein. Es gibt TRUE zurück, wenn der erste Parameter kleiner oder gleich ist wie der zweiter Parameter oder der erste Parameter zeitlich vor oder gleich dem zweiten Parameter ist.
                GREATER_THAN erwartet zwei Parameter. Diese Parameter können Zahlen oder Zeiten oder Daten oder Daten mit Zeiten sein. Es gibt TRUE zurück, wenn der erste Parameter grösser ist als der zweiter Parameter oder der erste Parameter zeitlich nach dem zweiten Parameter liegt.
                GREATER_EQUAL_THAN erwartet zwei Parameter. Diese Parameter können Zahlen oder Zeiten oder Daten oder Daten mit Zeiten sein. Es gibt TRUE zurück, wenn der erste Parameter grösser oder gleich ist wie der zweiter Parameter oder der erste Parameter zeitlich nach oder gleich dem zweiten Parameter ist.
                LENGTH erwartet einen Parameter, entweder ein String oder eine Liste. Ist der Parameter ein String, gibt es die Länge des Strings zurück. Ist der Parameter eine Liste, gibt es die Anzahl der Elemente in der Liste zurück.
                MIN_MAX_LENGTH erwartet drei Parameter. Der erste Parameter ist eine Zahl oder ein String. Der zweite und dritte Parameter sind Zahlen. Es gibt TRUE zurück, wenn der erste Parameter grösser ist als der zweite Parameter und gleichzeitig kleiner als der dritte Parameter. Ist der erste Parameter ein String, so wird die Länge des Strings verglichen.
                END_OF_DAY erwartet einen Parameter. Der Parameter ist entweder ein Datum oder ein Datum mit Zeit als String. In beiden Fällen gibt es dasselbe Datum zurück mit einer Uhrzeit um 23:59:59.
                START_OF_DAY erwartet einen Parameter. Der Parameter ist entweder ein Datum oder ein Datum mit Zeit als String. In beiden Fällen gibt es dasselbe Datum zurück mit einer Uhrzeit um 00:00:00.
                AS_DATE erwartet einen Parameter als String. Es parsed den Parameter und gibt diesen als Datum-Objekt zurück.
                AS_TIME erwartet einen Parameter als String. Es parsed den Parameter und gibt diesen als Zeit-Objekt zurück.
                AS_DATETIME erwartet einen Parameter als String. Es parsed den Parameter und gibt diesen als Datum-mit-Zeit-Objekt zurück.
                CONTAINS erwartet zwei Parameter. Der erste Parameter ist ein primitiver Datentyp, der zweite Parameter eine Liste. Es gibt TRUE zurück, wenn die Liste (der zweite Parameter) den primitiven Wert (erster Parameter) enthält.
                AS_LIST erwartet beliebig viele Parameter. Es gibt eine Liste zurück, welche alle Parameter in derselben Reihenfolge enthält.
                IS_UPPERCASE erwartet einen Parameter als String. Es gibt TRUE zurück, wenn alle Buchstaben des Strings Grossbuchstaben sind.
                AS_STRING erwartet einen Parameter von beliebigem Typ. Es gibt die String-Repräsentation des Parameters zurück.
                REGEX erwartet zwei Parameter als String. Der erste Parameter is eine Regular Expression in Java-Syntax. Der zweite Parameter ist ein beliebiger String. Es gibt TRUE zurück, wenn der zweite Parameter die Regular Expression erfüllt.
                ```
                
                Antworte als JSON konform zu der folgenden Grammatik:
                ```
                E := F L
                F := NOT | EQUAL | AND | OR | LESS_THAN | LESS_EQUAL_THAN | GREATER_THAN | GREATER_EQUAL_THAN | LENGTH | MIN_MAX_LENGTH | END_OF_DAY | START_OF_DAY | AS_DATE | AS_TIME | AS_DATETIME | CONTAINS | AS_LIST | IS_UPPERCASE | AS_STRING | REGEX
                L := E L | L1
                L1 := V L1 | V
                V := '<variable>'
                ```
                
                Ersetze die Buchstaben in der Grammatik direkt mit den zugehörigen Funktionen und Parametern.
                Parameter, welche sich auf das Objekt beziehen, müssen den Prefix 'root.' haben. Im Objekt wird mittels Punktnotation navigiert.
                Elemente wie <variable> oder <empty> der Grammtik dürfen nicht im Ergebnis enthalten sein.
                Beachte, dass Funktionen unterschiedlich viele Parameter erwarten, wie oben beschrieben.
                """.formatted(ruleDescription, getDtoSchema(className));

        final String result = chatModel.call(prompt);
        return ruleTranslator.translateToRule(result);
    }

    private String getDtoSchema(final String className) {
        return """
                {
                  "type": "object",
                  "properties": {
                    "geschaeftNummer": {
                      "type": "string",
                      "description": "Die Geschäftsnummer."
                    },
                    "einreichungDatum": {
                      "type": "localdate",
                      "description": "Das Datum, an welchem das Geschäft eingereicht wurde."
                    },
                    "uebernahmeDatum": {
                      "type": "localdate",
                      "description": "Das Datum, an welchem das Geschäft in die Behandlung übernommen wurde."
                    },
                    "urheber": {
                      "type": "object",
                      "description": "Der Urheber des Geschäftes.",
                      "properties": {
                        "id": {
                          "type": "long",
                          "description": "Die eindeutige ID der Person."
                        },
                        "name": {
                          "type": "string",
                          "description": "Der Nachname der Person."
                        },
                        "vorname": {
                          "type": "string",
                          "description": "Der Vorname der Person."
                        }
                      }
                    },
                    "behandelndePersonen": {
                      "type": "array",
                      "description": "Die Personen, welche das Geschäft aktuell bearbeiten.",
                      "items": {
                        "type": "long",
                        "description": "Die eindeutige ID der Person."
                      }
                    }
                  }
                }
                """;
    }

}
