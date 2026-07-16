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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT extends PermissionContext {

    private static final String FIRST_VOCABULARY_CODE = "VOCA_CODE_1";
    private static final String SECOND_VOCABULARY_CODE = "VOCA_CODE_2";
    private static final String THIRD_VOCABULARY_CODE = "VOCA_CODE_3";
    private static final String CODE = "CODIE";

    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private MasterDataVocabularyService vocabularyService;
    @Autowired
    private MasterDataGenericEntityRepository MasterDataGenericEntityRepository;

    @BeforeEach
    public void setUp() {
        vocabularyRepository.deleteAllWithTx();

        vocabularyRepository.saveWithTx(Vocabulary.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE)
                .build());
        vocabularyRepository.saveWithTx(Vocabulary.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.SECOND_VOCABULARY_CODE)
                .build());
        vocabularyRepository.saveWithTx(Vocabulary.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.THIRD_VOCABULARY_CODE)
                .build());
    }

    @Test
    void retrieveCodesFromMultipleVocabularies() {
        final MasterDataGenericEntity masterdataGenericEntity1 = MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build();
        MasterDataGenericEntityRepository.saveWithTx(masterdataGenericEntity1);
        final MasterDataGenericEntity masterdataGenericEntity2 = MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.SECOND_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build();
        MasterDataGenericEntityRepository.saveWithTx(masterdataGenericEntity2);
        Assertions.assertWith(vocabularyService.getCodesForMultipleVocabularies(List.of(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE, DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.SECOND_VOCABULARY_CODE), Locale.GERMAN), responseVocabularies -> {
            Assertions.assertWith(responseVocabularies.get(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), responseCodes -> assertThat(responseCodes).hasSize(1));
            Assertions.assertWith(responseVocabularies.get(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.SECOND_VOCABULARY_CODE), responseCodes -> assertThat(responseCodes).hasSize(1));
        });
    }

    @Test
    void returnOnlyRequestedVocabularies() {
        final MasterDataGenericEntity masterdataGenericEntity1 = MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build();
        MasterDataGenericEntityRepository.saveWithTx(masterdataGenericEntity1);
        final MasterDataGenericEntity masterdataGenericEntity2 = MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.SECOND_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build();
        MasterDataGenericEntityRepository.saveWithTx(masterdataGenericEntity2);
        final MasterDataGenericEntity masterdataGenericEntity3 = MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.THIRD_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build();
        MasterDataGenericEntityRepository.saveWithTx(masterdataGenericEntity3);
        Assertions.assertWith(vocabularyService.getCodesForMultipleVocabularies(List.of(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE, DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.SECOND_VOCABULARY_CODE), Locale.GERMAN).keySet(), responseVocabularyCodes -> {
            assertThat(responseVocabularyCodes).hasSize(2);
            assertThat(responseVocabularyCodes).containsExactly(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE, DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.SECOND_VOCABULARY_CODE);
        });
    }

    @Test
    void retrieveSingleVocabulary() {
        final MasterDataGenericEntity masterDataGenericEntity = MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build();
        MasterDataGenericEntityRepository.saveWithTx(masterDataGenericEntity);
        Assertions.assertWith(vocabularyService.getCodesForMultipleVocabularies(List.of(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), Locale.GERMAN).get(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), responseCodes -> {
            assertThat(responseCodes).hasSize(1);
        });
    }

    @Test
    void getVocabulary_returnIncludesArchived() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        Assertions.assertWith(vocabularyService.getCodesForMultipleVocabularies(List.of(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), Locale.GERMAN).get(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), responseCodes -> {
            assertThat(responseCodes).hasSize(1);
            assertThat(responseCodes.get(0).getCode()).isEqualTo(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE + "_OTHER");
        });
    }

    @Test
    void getVocabulary_doesNotReturnOutdatedCodes() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(1950, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(1960, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        Assertions.assertWith(vocabularyService.getCodesForMultipleVocabularies(List.of(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), Locale.GERMAN).get(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), responseCodes ->
                assertThat(responseCodes).isEmpty());
    }

    @Test
    void getVocabulary_doesNotReturnNotYetActiveCodes() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2950, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2960, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        Assertions.assertWith(vocabularyService.getCodesForMultipleVocabularies(List.of(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), Locale.GERMAN).get(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), responseCodes ->
                assertThat(responseCodes).isEmpty());
    }

    @Test
    void getVocabulary_returnIncludesOnlyUserLanguage() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name DE")
                .longName("my long name DE")
                .archived(true)
                .build());
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE)
                .languageCode(Locale.FRENCH.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name FR")
                .longName("my long name FR")
                .archived(true)
                .build());
        Assertions.assertWith(vocabularyService.getCodesForMultipleVocabularies(List.of(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), Locale.GERMAN).get(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.FIRST_VOCABULARY_CODE), responseCodes -> {
            assertThat(responseCodes).hasSize(1);
            assertThat(responseCodes.get(0).getCode()).isEqualTo(DefaultMasterDataVocabularyService_getCodesForMultipleVocabularies_IT.CODE + "_OTHER");
            assertThat(responseCodes.get(0).getShortName()).isEqualTo("my short name DE");
        });
    }

}
