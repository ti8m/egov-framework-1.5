package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.mdm.PermissionContext;
import ch.ti8m.egov.mdm.api.types.DetailedCode;
import ch.ti8m.egov.mdm.api.types.TermTranslation;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;


@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class DefaultMasterDataVocabularyService_createCodeForVocabulary_IT extends PermissionContext {

    private static final String CODE = "CODE";
    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);
    private static final String VOCABULARY_CODE = "VOCA_CODE";

    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private MasterDataVocabularyService vocabularyService;
    @Autowired
    private MasterDataGenericEntityRepository MasterDataGenericEntityRepository;
    @MockBean // mocked so no exceptions are thrown
    private CodeRequestValidator codeRequestValidator;
    private long vocabularyId;

    @BeforeEach
    public void setUp() {
        vocabularyRepository.deleteAllWithTx();

        vocabularyId = vocabularyRepository.saveWithTx(Vocabulary.builder()
                .code(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.VOCABULARY_CODE)
                .modifiable(true)
                .build());
    }

    @Test
    void newCodeWithoutFutureValue_createCodeForVocabulary_newCodeCreated() {
        final DetailedCode code = new DetailedCode();
        code.setCode(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
        code.setValidTo(LocalDate.parse("2150-10-25"));
        code.setFields(Map.of(
                "additionalField", "some content"
        ));
        code.setCurrentValue(Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "some short name de",
                        "some long name de"
                )
        ));
        long codeId = vocabularyService.createCodeForVocabulary(vocabularyId, code);
        Assertions.assertThat(codeId).isGreaterThan(0);

        Assertions.assertWith(MasterDataGenericEntityRepository.findBy(MasterDataGenericEntity.Fields.code, DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE), codes -> {
            Assertions.assertThat(codes).hasSize(1);
            Assertions.assertWith(codes.get(0), current -> {
                Assertions.assertThat(current.isArchived()).isFalse();
                Assertions.assertThat(current.getCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
                Assertions.assertThat(current.getValidFrom()).isCloseTo(LocalDate.now().atStartOfDay(), Assertions.within(5, ChronoUnit.SECONDS));
                Assertions.assertThat(current.getValidTo()).isEqualTo(code.getValidTo().plusDays(1).atStartOfDay());
                Assertions.assertThat(current.getShortName()).isEqualTo(code.getCurrentValue().get(Locale.GERMAN.getLanguage()).getShortName());
                Assertions.assertThat(current.getLongName()).isEqualTo(code.getCurrentValue().get(Locale.GERMAN.getLanguage()).getLongName());
                Assertions.assertThat(current.getVocabularyCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.VOCABULARY_CODE);
                Assertions.assertThat(current.getLanguageCode()).isEqualTo(Locale.GERMAN.getLanguage());
                Assertions.assertThat(current.getAdditionalContent()).isEqualTo(code.getFields());
            });
        });
    }

    @Test
    void newCodeWithoutValidTo_createCodeForVocabulary_newCodeCreated() {
        final DetailedCode code = new DetailedCode();
        code.setCode(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
        code.setFields(Map.of(
                "additionalField", "some content"
        ));
        code.setCurrentValue(Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "some short name de",
                        "some long name de"
                )
        ));
        vocabularyService.createCodeForVocabulary(vocabularyId, code);

        Assertions.assertWith(MasterDataGenericEntityRepository.findBy(MasterDataGenericEntity.Fields.code, DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE), codes -> {
            Assertions.assertThat(codes).hasSize(1);
            Assertions.assertWith(codes.get(0), current -> {
                Assertions.assertThat(current.isArchived()).isFalse();
                Assertions.assertThat(current.getCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
                Assertions.assertThat(current.getValidFrom()).isCloseTo(LocalDate.now().atStartOfDay(), Assertions.within(5, ChronoUnit.SECONDS));
                Assertions.assertThat(current.getValidTo()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.MAX_DATE.atStartOfDay());
                Assertions.assertThat(current.getShortName()).isEqualTo(code.getCurrentValue().get(Locale.GERMAN.getLanguage()).getShortName());
                Assertions.assertThat(current.getLongName()).isEqualTo(code.getCurrentValue().get(Locale.GERMAN.getLanguage()).getLongName());
                Assertions.assertThat(current.getVocabularyCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.VOCABULARY_CODE);
                Assertions.assertThat(current.getLanguageCode()).isEqualTo(Locale.GERMAN.getLanguage());
                Assertions.assertThat(current.getAdditionalContent()).isEqualTo(code.getFields());
            });
        });
    }

    @Test
    void newCodeWithFutureValue_createCodeForVocabulary_newCodeCreated() {
        final DetailedCode code = new DetailedCode();
        code.setCode(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
        code.setValidTo(LocalDate.parse("2150-10-25"));
        code.setFields(Map.of(
                "additionalField", "some content"
        ));
        code.setCurrentValue(Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "some short name de",
                        "some long name de"
                )
        ));
        code.setFutureValue(Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "some short name de future",
                        "some long name de future"
                )
        ));
        vocabularyService.createCodeForVocabulary(vocabularyId, code);

        Assertions.assertWith(MasterDataGenericEntityRepository.findBy(MasterDataGenericEntity.Fields.code, DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE), codes -> {
            Assertions.assertThat(codes).hasSize(2);
            codes.sort(Comparator.comparing(MasterDataGenericEntity::getValidFrom));
            Assertions.assertWith(codes.get(0), current -> {
                Assertions.assertThat(current.isArchived()).isFalse();
                Assertions.assertThat(current.getCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
                Assertions.assertThat(current.getValidFrom()).isCloseTo(LocalDate.now().atStartOfDay(), Assertions.within(5, ChronoUnit.SECONDS));
                Assertions.assertThat(current.getValidTo()).isEqualTo(code.getValidTo().plusDays(1).atStartOfDay());
                Assertions.assertThat(current.getShortName()).isEqualTo(code.getCurrentValue().get(Locale.GERMAN.getLanguage()).getShortName());
                Assertions.assertThat(current.getLongName()).isEqualTo(code.getCurrentValue().get(Locale.GERMAN.getLanguage()).getLongName());
                Assertions.assertThat(current.getVocabularyCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.VOCABULARY_CODE);
                Assertions.assertThat(current.getLanguageCode()).isEqualTo(Locale.GERMAN.getLanguage());
                Assertions.assertThat(current.getAdditionalContent()).isEqualTo(code.getFields());
            });
            Assertions.assertWith(codes.get(1), future -> {
                Assertions.assertThat(future.isArchived()).isFalse();
                Assertions.assertThat(future.getCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
                Assertions.assertThat(future.getValidFrom()).isEqualTo(code.getValidTo().plusDays(1).atStartOfDay());
                Assertions.assertThat(future.getValidTo()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.MAX_DATE.atStartOfDay());
                Assertions.assertThat(future.getShortName()).isEqualTo(code.getFutureValue().get(Locale.GERMAN.getLanguage()).getShortName());
                Assertions.assertThat(future.getLongName()).isEqualTo(code.getFutureValue().get(Locale.GERMAN.getLanguage()).getLongName());
                Assertions.assertThat(future.getVocabularyCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.VOCABULARY_CODE);
                Assertions.assertThat(future.getLanguageCode()).isEqualTo(Locale.GERMAN.getLanguage());
                Assertions.assertThat(future.getAdditionalContent()).isEqualTo(code.getFields());
            });
        });
    }

    @Test
    void newCodeWithMultipleFutureValues_createCodeForVocabulary_newCodeCreated() {
        final DetailedCode code = new DetailedCode();
        code.setCode(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
        code.setValidTo(LocalDate.parse("2150-10-25"));
        code.setFields(Map.of(
                "additionalField", "some content"
        ));
        code.setCurrentValue(Map.of());
        code.setFutureValue(Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "some short name de",
                        "some long name de"
                ),
                Locale.FRENCH.getLanguage(), new TermTranslation(
                        "some short name de future",
                        "some long name de future"
                )
        ));
        vocabularyService.createCodeForVocabulary(vocabularyId, code);

        Assertions.assertWith(MasterDataGenericEntityRepository.findBy(MasterDataGenericEntity.Fields.code, DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE), codes -> {
            Assertions.assertThat(codes).hasSize(2);
            codes.sort(Comparator.comparing(MasterDataGenericEntity::getLanguageCode));
            Assertions.assertWith(codes.get(0), german -> {
                Assertions.assertThat(german.isArchived()).isFalse();
                Assertions.assertThat(german.getCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
                Assertions.assertThat(german.getValidFrom()).isEqualTo(code.getValidTo().plusDays(1).atStartOfDay());
                Assertions.assertThat(german.getValidTo()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.MAX_DATE.atStartOfDay());
                Assertions.assertThat(german.getShortName()).isEqualTo(code.getFutureValue().get(Locale.GERMAN.getLanguage()).getShortName());
                Assertions.assertThat(german.getLongName()).isEqualTo(code.getFutureValue().get(Locale.GERMAN.getLanguage()).getLongName());
                Assertions.assertThat(german.getVocabularyCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.VOCABULARY_CODE);
                Assertions.assertThat(german.getLanguageCode()).isEqualTo(Locale.GERMAN.getLanguage());
                Assertions.assertThat(german.getAdditionalContent()).isEqualTo(code.getFields());
            });
            Assertions.assertWith(codes.get(1), french -> {
                Assertions.assertThat(french.isArchived()).isFalse();
                Assertions.assertThat(french.getCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
                Assertions.assertThat(french.getValidFrom()).isEqualTo(code.getValidTo().plusDays(1).atStartOfDay());
                Assertions.assertThat(french.getValidTo()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.MAX_DATE.atStartOfDay());
                Assertions.assertThat(french.getShortName()).isEqualTo(code.getFutureValue().get(Locale.FRENCH.getLanguage()).getShortName());
                Assertions.assertThat(french.getLongName()).isEqualTo(code.getFutureValue().get(Locale.FRENCH.getLanguage()).getLongName());
                Assertions.assertThat(french.getVocabularyCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.VOCABULARY_CODE);
                Assertions.assertThat(french.getLanguageCode()).isEqualTo(Locale.FRENCH.getLanguage());
                Assertions.assertThat(french.getAdditionalContent()).isEqualTo(code.getFields());
            });
        });
    }

    @Test
    void newCodeWithMultipleLanguages_createCodeForVocabulary_newCodeCreated() {
        final DetailedCode code = new DetailedCode();
        code.setCode(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
        code.setValidTo(LocalDate.parse("2150-10-25"));
        code.setFields(Map.of(
                "additionalField", "some content"
        ));
        code.setCurrentValue(Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "some short name de",
                        "some long name de"
                ),
                Locale.FRENCH.getLanguage(), new TermTranslation(
                        "some short name fr",
                        "some long name fr"
                )
        ));
        vocabularyService.createCodeForVocabulary(vocabularyId, code);

        Assertions.assertWith(MasterDataGenericEntityRepository.findBy(MasterDataGenericEntity.Fields.code, DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE), codes -> {
            Assertions.assertThat(codes).hasSize(2);
            codes.sort(Comparator.comparing(MasterDataGenericEntity::getLanguageCode));
            Assertions.assertWith(codes.get(0), german -> {
                Assertions.assertThat(german.isArchived()).isFalse();
                Assertions.assertThat(german.getCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
                Assertions.assertThat(german.getValidFrom()).isCloseTo(LocalDate.now().atStartOfDay(), Assertions.within(5, ChronoUnit.SECONDS));
                Assertions.assertThat(german.getValidTo()).isEqualTo(code.getValidTo().plusDays(1).atStartOfDay());
                Assertions.assertThat(german.getShortName()).isEqualTo(code.getCurrentValue().get(Locale.GERMAN.getLanguage()).getShortName());
                Assertions.assertThat(german.getLongName()).isEqualTo(code.getCurrentValue().get(Locale.GERMAN.getLanguage()).getLongName());
                Assertions.assertThat(german.getVocabularyCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.VOCABULARY_CODE);
                Assertions.assertThat(german.getLanguageCode()).isEqualTo(Locale.GERMAN.getLanguage());
                Assertions.assertThat(german.getAdditionalContent()).isEqualTo(code.getFields());
            });
            Assertions.assertWith(codes.get(1), french -> {
                Assertions.assertThat(french.isArchived()).isFalse();
                Assertions.assertThat(french.getCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.CODE);
                Assertions.assertThat(french.getValidFrom()).isCloseTo(LocalDate.now().atStartOfDay(), Assertions.within(5, ChronoUnit.SECONDS));
                Assertions.assertThat(french.getValidTo()).isEqualTo(code.getValidTo().plusDays(1).atStartOfDay());
                Assertions.assertThat(french.getShortName()).isEqualTo(code.getCurrentValue().get(Locale.FRENCH.getLanguage()).getShortName());
                Assertions.assertThat(french.getLongName()).isEqualTo(code.getCurrentValue().get(Locale.FRENCH.getLanguage()).getLongName());
                Assertions.assertThat(french.getVocabularyCode()).isEqualTo(DefaultMasterDataVocabularyService_createCodeForVocabulary_IT.VOCABULARY_CODE);
                Assertions.assertThat(french.getLanguageCode()).isEqualTo(Locale.FRENCH.getLanguage());
                Assertions.assertThat(french.getAdditionalContent()).isEqualTo(code.getFields());
            });
        });
    }

}
