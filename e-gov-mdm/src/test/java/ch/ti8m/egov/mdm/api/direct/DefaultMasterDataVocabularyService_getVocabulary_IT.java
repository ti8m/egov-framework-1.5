package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.mdm.PermissionContext;
import ch.ti8m.egov.mdm.persistence.entity.FieldDefinition;
import ch.ti8m.egov.mdm.persistence.entity.LanguageDefinition;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import ch.ti8m.egov.mdm.persistence.entity.VocabularyLn;
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

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class DefaultMasterDataVocabularyService_getVocabulary_IT extends PermissionContext {

    private static final String VOCABULARY_CODE_ALLE = "VOCA_CODE";
    private static final String CODE = "CODIE";
    private static final String VOCABULARY_LONG_NAME = "long name";
    private static final String VOCABULARY_SHORT_NAME = "short name";
    private static final Long PARENT_VOCABULARY_ID = 123L;
    private static final String REFERENCE_FIELD_CODE = "ref field code";
    private static final String STRING_FIELD_CODE = "string field code";

    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private MasterDataVocabularyService vocabularyService;
    @Autowired
    private MasterDataGenericEntityRepository MasterDataGenericEntityRepository;
    private long vocaCodeVocabularyId;

    @BeforeEach
    public void setUp() {
        vocabularyRepository.deleteAllWithTx();

        final Vocabulary vocabulary = Vocabulary.builder()
                .code(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_CODE_ALLE)
                .localizations(List.of(
                        VocabularyLn.builder()
                                .languageCode(PermissionContext.USER_LANGUAGE)
                                .name(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_SHORT_NAME)
                                .description(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_LONG_NAME)
                                .build()
                ))
                .languages(List.of(
                        LanguageDefinition.builder()
                                .languageCode(Locale.FRENCH.getLanguage())
                                .build()
                ))
                .fields(List.of(
                        FieldDefinition.builder()
                                .defaultValue(DefaultMasterDataVocabularyService_getVocabulary_IT.PARENT_VOCABULARY_ID.toString())
                                .type(Long.class.getName())
                                .code(DefaultMasterDataVocabularyService_getVocabulary_IT.REFERENCE_FIELD_CODE)
                                .build(),
                        FieldDefinition.builder()
                                .type(String.class.getName())
                                .code(DefaultMasterDataVocabularyService_getVocabulary_IT.STRING_FIELD_CODE)
                                .build()
                ))
                .build();
        vocabulary.getLocalization(PermissionContext.USER_LANGUAGE).orElseThrow().setVocabulary(vocabulary);
        vocabulary.getLanguages().get(0).setVocabulary(vocabulary);
        vocabulary.getFields().get(0).setVocabulary(vocabulary);
        vocabulary.getFields().get(1).setVocabulary(vocabulary);
        vocaCodeVocabularyId = vocabularyRepository.saveWithTx(vocabulary);
    }

    @Test
    void retrieveVocabulary() {
        final MasterDataGenericEntity masterDataGenericEntity = MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getVocabulary_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build();
        MasterDataGenericEntityRepository.saveWithTx(masterDataGenericEntity);
        Assertions.assertWith(vocabularyService.getVocabulary(vocaCodeVocabularyId, Locale.GERMAN), vocabulary -> {
            Assertions.assertThat(vocabulary.getVocabularyId()).isEqualTo(vocaCodeVocabularyId);
            Assertions.assertThat(vocabulary.getLongName()).isEqualTo(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_LONG_NAME);
            Assertions.assertThat(vocabulary.getShortName()).isEqualTo(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_SHORT_NAME);
            Assertions.assertThat(vocabulary.getLanguages()).containsAll(List.of(Locale.FRENCH.getLanguage()));
            Assertions.assertWith(vocabulary.getFields().get(0), fieldDefinition -> {
                Assertions.assertThat(fieldDefinition.getMachineName()).isEqualTo(DefaultMasterDataVocabularyService_getVocabulary_IT.REFERENCE_FIELD_CODE);
                Assertions.assertThat(fieldDefinition.getParentVocabulary()).isEqualTo(DefaultMasterDataVocabularyService_getVocabulary_IT.PARENT_VOCABULARY_ID);
                Assertions.assertThat(fieldDefinition.getType()).isEqualTo(Long.class.getName());
            });
            Assertions.assertWith(vocabulary.getFields().get(1), fieldDefinition -> {
                Assertions.assertThat(fieldDefinition.getMachineName()).isEqualTo(DefaultMasterDataVocabularyService_getVocabulary_IT.STRING_FIELD_CODE);
                Assertions.assertThat(fieldDefinition.getType()).isEqualTo(String.class.getName());
            });
            Assertions.assertThat(vocabulary.getCodes()).hasSize(1);
            Assertions.assertWith(vocabulary.getCodes().get(0), code -> {
                Assertions.assertThat(code.getCode()).isEqualTo(masterDataGenericEntity.getCode());
                Assertions.assertThat(code.getShortName()).isEqualTo(masterDataGenericEntity.getShortName());
                Assertions.assertThat(code.getCodeId()).isEqualTo(masterDataGenericEntity.getId());
            });
        });
    }

    @Test
    void getVocabulary_returnIncludesArchived() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        Assertions.assertWith(vocabularyService.getVocabulary(vocaCodeVocabularyId, Locale.GERMAN), vocabulary -> {
            Assertions.assertThat(vocabulary.getCodes()).hasSize(1);
            Assertions.assertThat(vocabulary.getCodes().get(0).getCode()).isEqualTo(DefaultMasterDataVocabularyService_getVocabulary_IT.CODE + "_OTHER");
        });
    }

    @Test
    void getVocabulary_doesNotReturnOutdatedCodes() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(1950, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(1960, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        Assertions.assertWith(vocabularyService.getVocabulary(vocaCodeVocabularyId, Locale.GERMAN), vocabulary ->
                Assertions.assertThat(vocabulary.getCodes()).isEmpty());
    }

    @Test
    void getVocabulary_doesNotReturnNotYetActiveCodes() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2950, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2960, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        Assertions.assertWith(vocabularyService.getVocabulary(vocaCodeVocabularyId, Locale.GERMAN), vocabulary ->
                Assertions.assertThat(vocabulary.getCodes()).isEmpty());
    }

    @Test
    void getVocabulary_returnIncludesOnlyUserLanguage() {
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name DE")
                .longName("my long name DE")
                .archived(true)
                .build());
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_getVocabulary_IT.CODE + "_OTHER")
                .vocabularyCode(DefaultMasterDataVocabularyService_getVocabulary_IT.VOCABULARY_CODE_ALLE)
                .languageCode(Locale.FRENCH.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name FR")
                .longName("my long name FR")
                .archived(true)
                .build());
        Assertions.assertWith(vocabularyService.getVocabulary(vocaCodeVocabularyId, Locale.GERMAN), vocabulary -> {
            Assertions.assertThat(vocabulary.getCodes()).hasSize(1);
            Assertions.assertThat(vocabulary.getCodes().get(0).getCode()).isEqualTo(DefaultMasterDataVocabularyService_getVocabulary_IT.CODE + "_OTHER");
            Assertions.assertThat(vocabulary.getCodes().get(0).getShortName()).isEqualTo("my short name DE");
        });
    }

}
