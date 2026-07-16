package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.mdm.PermissionContext;
import ch.ti8m.egov.mdm.api.types.Code;
import ch.ti8m.egov.mdm.api.types.TermTranslation;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Locale.GERMAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertWith;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class VocabularyRepository_updateCodeForVocabulary_IT extends PermissionContext {

    private static final String VOCABULARY_CODE = "VOCA_CODE";
    private static final String CODE = "CODE";
    private static final LocalDateTime CURRENT_DATE_TIME = LocalDateTime.now();
    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);

    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private MasterDataVocabularyService vocabularyService;
    @Autowired
    private MasterDataGenericEntityRepository masterDataGenericEntityRepository;
    @MockBean // mocked so no exceptions are thrown
    private CodeRequestValidator codeRequestValidator;
    private long vocabularyId;
    private long currentCodeId;
    private long futureCodeId;

    @BeforeEach
    public void setUp() {
        vocabularyRepository.deleteAllWithTx();

        vocabularyId = vocabularyRepository.saveWithTx(Vocabulary.builder()
                .code(VocabularyRepository_updateCodeForVocabulary_IT.VOCABULARY_CODE)
                .modifiable(true)
                .build());
        currentCodeId = masterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(VocabularyRepository_updateCodeForVocabulary_IT.CODE)
                .vocabularyCode(VocabularyRepository_updateCodeForVocabulary_IT.VOCABULARY_CODE)
                .validFrom(VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME.minusDays(5))
                .validTo(VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME.plusDays(5))
                .languageCode(GERMAN.getLanguage())
                .build());
        futureCodeId = masterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(VocabularyRepository_updateCodeForVocabulary_IT.CODE)
                .vocabularyCode(VocabularyRepository_updateCodeForVocabulary_IT.VOCABULARY_CODE)
                .validFrom(VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME.plusDays(6))
                .validTo(VocabularyRepository_updateCodeForVocabulary_IT.MAX_DATE.atStartOfDay())
                .languageCode(GERMAN.getLanguage())
                .build());
    }

    @Test
    void validInput_updateCodeForVocabulary_existingCodeIsTerminated() {
        vocabularyService.updateCodeForVocabulary(vocabularyId, currentCodeId, createValidCodeRequest());

        final MasterDataGenericEntity MasterDataGenericEntity = masterDataGenericEntityRepository.findById(currentCodeId).orElseThrow();
        assertThat(MasterDataGenericEntity.getValidTo()).isEqualTo(LocalDate.now().atStartOfDay());
    }

    @Test
    void validInput_updateCodeForVocabulary_futureVersionIsDeleted() {
        vocabularyService.updateCodeForVocabulary(vocabularyId, currentCodeId, createValidCodeRequest());

        assertThat(masterDataGenericEntityRepository.findById(futureCodeId)).isNotPresent();
    }

    @Test
    void validInput_updateCodeForVocabulary_newCurrentVersionIsCreated() {
        vocabularyService.updateCodeForVocabulary(vocabularyId, currentCodeId, createValidCodeRequest());
        final List<MasterDataGenericEntity> newCurrentVersions = masterDataGenericEntityRepository.findWithFilter(
                MasterDataGenericEntity.Fields.code + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.CODE + "'",
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.VOCABULARY_CODE + "'",
                MasterDataGenericEntity.Fields.validFrom + " < " + VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME,
                MasterDataGenericEntity.Fields.validTo + " > " + VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME
        );
        assertThat(newCurrentVersions).hasSize(1);
        assertWith(newCurrentVersions.get(0), currentVersion -> {
            assertThat(currentVersion.getId()).isNotEqualTo(currentCodeId);
            assertThat(currentVersion.getLongName()).isEqualTo("some long name de");
        });
    }

    @Test
    void validInputWithTwoLocalizations_updateCodeForVocabulary_newCurrentVersionIsCreated() {
        final Code code = createValidCodeRequest();
        code.setCurrentValue(
                Map.of(
                        GERMAN.getLanguage(), new TermTranslation(
                                "some short name de",
                                "some long name de"
                        ),
                        Locale.FRENCH.getLanguage(), new TermTranslation(
                                "some short name fr",
                                "some long name fr"
                        )
                )
        );
        vocabularyService.updateCodeForVocabulary(vocabularyId, currentCodeId, code);
        final List<MasterDataGenericEntity> newCurrentVersions = masterDataGenericEntityRepository.findWithFilter(
                MasterDataGenericEntity.Fields.code + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.CODE + "'",
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.VOCABULARY_CODE + "'",
                MasterDataGenericEntity.Fields.validFrom + " < " + VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME,
                MasterDataGenericEntity.Fields.validTo + " > " + VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME
        );
        assertThat(newCurrentVersions).hasSize(2);
        newCurrentVersions.forEach(currentVersion -> assertThat(currentVersion.getId()).isNotEqualTo(currentCodeId));
    }

    @Test
    void validInput_updateCodeForVocabulary_newFutureVersionIsCreated() {
        vocabularyService.updateCodeForVocabulary(vocabularyId, currentCodeId, createValidCodeRequest());
        final List<MasterDataGenericEntity> newFutureVersions = masterDataGenericEntityRepository.findWithFilter(
                MasterDataGenericEntity.Fields.code + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.CODE + "'",
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.VOCABULARY_CODE + "'",
                MasterDataGenericEntity.Fields.validFrom + " > " + VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME
        );
        assertThat(newFutureVersions).hasSize(1);
        assertWith(newFutureVersions.get(0), futureVersion -> {
            assertThat(futureVersion.getId()).isNotEqualTo(futureCodeId);
            assertThat(futureVersion.getLongName()).isEqualTo("some future long name de");
        });
    }

    @Test
    void validInputWithoutFutureVersion_updateCodeForVocabulary_newVersionIsCreatedButNoFutureVersion() {
        final Code code = createValidCodeRequest();
        code.setFutureValue(null);
        vocabularyService.updateCodeForVocabulary(vocabularyId, currentCodeId, code);

        final List<MasterDataGenericEntity> newCurrentVersions = masterDataGenericEntityRepository.findWithFilter(
                MasterDataGenericEntity.Fields.code + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.CODE + "'",
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.VOCABULARY_CODE + "'",
                MasterDataGenericEntity.Fields.validFrom + " < " + VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME,
                MasterDataGenericEntity.Fields.validTo + " > " + VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME
        );
        assertThat(newCurrentVersions).hasSize(1);

        final List<MasterDataGenericEntity> newFutureVersions = masterDataGenericEntityRepository.findWithFilter(
                MasterDataGenericEntity.Fields.code + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.CODE + "'",
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.VOCABULARY_CODE + "'",
                MasterDataGenericEntity.Fields.validFrom + " > " + VocabularyRepository_updateCodeForVocabulary_IT.CURRENT_DATE_TIME
        );
        assertThat(newFutureVersions).isEmpty();
    }

    @Test
    void validInputWithoutValidTo_updateCodeForVocabulary_validToIsImplicitlySetToMaxDate() {
        final Code code = createValidCodeRequest();
        code.setFutureValue(null);
        code.setValidTo(null);
        vocabularyService.updateCodeForVocabulary(vocabularyId, currentCodeId, code);

        final var newCurrentVersions = masterDataGenericEntityRepository.findWithFilter(
                MasterDataGenericEntity.Fields.code + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.CODE + "'",
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + VocabularyRepository_updateCodeForVocabulary_IT.VOCABULARY_CODE + "'",
                MasterDataGenericEntity.Fields.validFrom + " < " + LocalDateTime.now(),
                MasterDataGenericEntity.Fields.validTo + " > " + LocalDateTime.now()
        );

        assertThat(newCurrentVersions).hasSize(1);
        assertWith(newCurrentVersions.get(0), futureVersion -> {
            assertThat(futureVersion.getValidFrom()).isEqualTo(LocalDate.now().atStartOfDay());
            assertThat(futureVersion.getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31).atStartOfDay());
        });
    }

    private Code createValidCodeRequest() {
        final Code code = new Code();

        code.setCurrentValue(Map.of(GERMAN.getLanguage(), new TermTranslation("some short name de", "some long name de")));
        code.setFutureValue(Map.of(GERMAN.getLanguage(), new TermTranslation("some future short name de", "some future long name de")));
        code.setFields(Map.of("additionalField", "some content"));
        code.setValidTo(LocalDate.parse("2150-10-25"));

        return code;
    }

}
