package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.mdm.PermissionContext;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DefaultMasterDataVocabularyService_getTranslatedTerms_IT extends PermissionContext {

    private static final String VOCABULARY_CODE = "VOCA_CODE";
    private static final String ACTIVE_CODE = "CODE_1";
    private static final String EXPIRED_CODE = "CODE_3";
    private static final String ACTIVE_LONG_NAME_GERMAN = "active long name DE";
    private static final String ACTIVE_LONG_NAME_FRENCH = "active long name FR";
    private static final String EXPIRED_LONG_NAME = "expired long name";

    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private MasterDataGenericEntityRepository MasterDataGenericEntityRepository;
    @Autowired
    private MasterDataVocabularyService vocabularyService;

    @BeforeEach
    public void setUp() {
        vocabularyRepository.saveWithTx(Vocabulary.builder()
                .code(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.VOCABULARY_CODE)
                .build());
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.ACTIVE_CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.VOCABULARY_CODE)
                .longName(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.ACTIVE_LONG_NAME_GERMAN)
                .validFrom(LocalDateTime.now().minusDays(5))
                .validTo(LocalDateTime.now().plusDays(5))
                .languageCode(Locale.GERMAN.getLanguage())
                .archived(false)
                .build());
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.ACTIVE_CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.VOCABULARY_CODE)
                .longName(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.ACTIVE_LONG_NAME_FRENCH)
                .validFrom(LocalDateTime.now().minusDays(5))
                .validTo(LocalDateTime.now().plusDays(5))
                .languageCode(Locale.FRENCH.getLanguage())
                .archived(false)
                .build());
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.EXPIRED_CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.VOCABULARY_CODE)
                .longName(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.EXPIRED_LONG_NAME)
                .validFrom(LocalDateTime.now().minusDays(5))
                .validTo(LocalDateTime.now().minusDays(3))
                .languageCode(Locale.GERMAN.getLanguage())
                .archived(true)
                .build());
    }

    @Test
    void correctInput_getTranslatedTermForUser_returnsTranslatedTerm() {
        final Map<String, String> translations = vocabularyService.getTranslatedTerms(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.VOCABULARY_CODE, DefaultMasterDataVocabularyService_getTranslatedTerms_IT.ACTIVE_CODE);
        Assertions.assertThat(translations.get(Locale.GERMAN.getLanguage())).isEqualTo(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.ACTIVE_LONG_NAME_GERMAN);
        Assertions.assertThat(translations.get(Locale.FRENCH.getLanguage())).isEqualTo(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.ACTIVE_LONG_NAME_FRENCH);
    }

    @Test
    void requestForExpiredLocalization_getTranslatedTermForUser_returnsNull() {
        final Map<String, String> translations = vocabularyService.getTranslatedTerms(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.VOCABULARY_CODE, DefaultMasterDataVocabularyService_getTranslatedTerms_IT.EXPIRED_CODE);
        Assertions.assertThat(translations).isEmpty();
    }

    @Test
    void unknownVocabulary_getTranslatedTermForUser_returnsNull() {
        final Map<String, String> translations = vocabularyService.getTranslatedTerms(DefaultMasterDataVocabularyService_getTranslatedTerms_IT.VOCABULARY_CODE + "_unknown", DefaultMasterDataVocabularyService_getTranslatedTerms_IT.ACTIVE_CODE);
        Assertions.assertThat(translations).isEmpty();
    }

}
