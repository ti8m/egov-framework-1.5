package ch.ti8m.egov.mdm.persistence;

import ch.ti8m.egov.mdm.PermissionContext;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.persistence.description.MDVocabulary;
import ch.ti8m.egov.mdm.persistence.entity.LanguageDefinition;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import ch.ti8m.egov.mdm.testbase.Ort;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static java.util.Locale.FRENCH;
import static java.util.Locale.GERMAN;
import static java.util.Locale.ITALIAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest
@ActiveProfiles("test")
class MasterdataVocabularyScannerTest extends PermissionContext {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Test
    @Transactional
    public void checkOrtVocabularyInitialization() {
        final Optional<Vocabulary> optionalVocabulary = vocabularyRepository.findOneBy("code", Ort.class.getAnnotation(MDVocabulary.class).code());
        assertWith(optionalVocabulary.orElseThrow(), vocabulary -> {
            assertThat(vocabulary.getCode()).isEqualTo(Ort.class.getAnnotation(MDVocabulary.class).code());
            assertThat(vocabulary.getLanguages().stream().map(LanguageDefinition::getLanguageCode).toList())
                    .containsAll(Arrays.stream(Ort.class.getAnnotation(MDVocabulary.class).availableLanguages()).toList());
            assertThat(vocabulary.getLocalizations()).isNotEmpty();
            vocabulary.getLocalizations().forEach(localization -> {
                if (localization.getLanguageCode().equals(GERMAN.getLanguage())) {
                    assertThat(localization.getName()).isEqualTo("Ort");
                    assertThat(localization.getDescription()).isEqualTo("CH-Ortschaften gemäss PLZ-Verzeichnis der Post, mit Angabe des Kantons");
                } else if (localization.getLanguageCode().equals(FRENCH.getLanguage())) {
                    assertThat(localization.getName()).isEqualTo("Lieu");
                    assertThat(localization.getDescription()).isEqualTo("Villes et villages en Suisse selon le répertoire des records postaux de la Poste suisse, indiquant le canton");
                } else {
                    fail("not a defined translation");
                }
            });
            assertThat(vocabulary.getFields()).isNotEmpty();
            vocabulary.getFields().forEach(field -> {
                if (field.getCode().equals("PLZ")) {
                    assertThat(field.getType()).isEqualTo(String.class.getName());
                    assertThat(field.getLocalizations()).isNotEmpty();


                    field.getLocalizations().forEach(fieldDefinitionLn -> {
                        final Locale languageCode = new Locale(fieldDefinitionLn.getLanguageCode());

                        if (languageCode.equals(GERMAN)) {
                            assertThat(fieldDefinitionLn.getName()).isEqualTo("PLZ DE");
                        } else if (languageCode.equals(FRENCH)) {
                            assertThat(fieldDefinitionLn.getName()).isEqualTo("PLZ FR");
                        } else if (languageCode.equals(ITALIAN)) {
                            assertThat(fieldDefinitionLn.getName()).isEqualTo("PLZ IT");
                        } else {
                            Assertions.fail("not a defined translation");
                        }
                    });
                } else {
                    Assertions.fail("not a defined field");
                }
            });
        });
    }

}
