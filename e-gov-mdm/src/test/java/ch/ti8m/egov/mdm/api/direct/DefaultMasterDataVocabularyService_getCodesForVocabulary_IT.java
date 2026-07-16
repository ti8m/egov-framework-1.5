package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.mdm.PermissionContext;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class DefaultMasterDataVocabularyService_getCodesForVocabulary_IT extends PermissionContext {

    private static final String VOCABULARY_CODE_ALLE = "VOCA_CODE";
    private static final String CODE = "CODIE";

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private DefaultMasterDataVocabularyService vocabularyService;

    @Autowired
    private MasterDataGenericEntityRepository MasterDataGenericEntityRepository;

    private long vocaCodeVocabularyId;

    @BeforeEach
    public void setUp() {
        vocabularyRepository.deleteAllWithTx();

        final Vocabulary vocabulary = Vocabulary.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.VOCABULARY_CODE_ALLE)
                .build();
        vocaCodeVocabularyId = vocabularyRepository.saveWithTx(vocabulary);
    }

    @Test
    void retrieveVocabulary() {
        final MasterDataGenericEntity masterDataGenericEntity = MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build();
        MasterDataGenericEntityRepository.saveWithTx(masterDataGenericEntity);
        assertWith(vocabularyService.getCodesForVocabulary(vocaCodeVocabularyId, Locale.GERMAN), responseCodes -> {
            assertThat(responseCodes).hasSize(1);
        });
    }

    @Test
    void getVocabulary_returnIncludesArchived() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        assertWith(vocabularyService.getCodesForVocabulary(vocaCodeVocabularyId, Locale.GERMAN), responseCodes -> {
            assertThat(responseCodes).hasSize(1);
            assertThat(responseCodes.get(0).getCode()).isEqualTo(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.CODE + "_OTHER");
        });
    }

    @Test
    void getVocabulary_doesNotReturnOutdatedCodes() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(1950, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(1960, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        assertWith(vocabularyService.getCodesForVocabulary(vocaCodeVocabularyId, Locale.GERMAN), responseCodes ->
                assertThat(responseCodes).isEmpty());
    }

    @Test
    void getVocabulary_doesNotReturnNotYetActiveCodes() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2950, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2960, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        assertWith(vocabularyService.getCodesForVocabulary(vocaCodeVocabularyId, Locale.GERMAN), responseCodes ->
                assertThat(responseCodes).isEmpty());
    }

    @Test
    void getVocabulary_returnIncludesOnlyUserLanguage() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name DE")
                .longName("my long name DE")
                .archived(true)
                .build());
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.FRENCH.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name FR")
                .longName("my long name FR")
                .archived(true)
                .build());
        assertWith(vocabularyService.getCodesForVocabulary(vocaCodeVocabularyId, Locale.GERMAN), responseCodes -> {
            assertThat(responseCodes).hasSize(1);
            assertThat(responseCodes.get(0).getCode()).isEqualTo(DefaultMasterDataVocabularyService_getCodesForVocabulary_IT.CODE + "_OTHER");
            assertThat(responseCodes.get(0).getShortName()).isEqualTo("my short name DE");
        });
    }
}
