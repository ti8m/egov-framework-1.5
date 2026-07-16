package ch.ti8m.egov.mdm.testbase;

import ch.ti8m.egov.mdm.api.abstraction.Masterdata;
import ch.ti8m.egov.mdm.persistence.description.MDLabel;
import ch.ti8m.egov.mdm.persistence.description.MDLabelTranslation;
import ch.ti8m.egov.mdm.persistence.description.MDVocabulary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@MDVocabulary(
        code = "ORTSCHAFTEN",
        translations = {
                @MDLabelTranslation(language = MDLabelTranslation.GERMAN, name = "Ort", description = "CH-Ortschaften gemäss PLZ-Verzeichnis der Post, mit Angabe des Kantons"),
                @MDLabelTranslation(language = MDLabelTranslation.FRENCH, name = "Lieu", description = "Villes et villages en Suisse selon le répertoire des records postaux de la Poste suisse, indiquant le canton")
        }
)
public class Ort extends Masterdata {
  @MDLabel(
          code = "PLZ",
          translations = {
                  @MDLabelTranslation(language = MDLabelTranslation.GERMAN, name = "PLZ DE"),
                  @MDLabelTranslation(language = MDLabelTranslation.FRENCH, name = "PLZ FR"),
                  @MDLabelTranslation(language = MDLabelTranslation.ITALIAN, name = "PLZ IT")
          }
  )
  private String plz;
}
