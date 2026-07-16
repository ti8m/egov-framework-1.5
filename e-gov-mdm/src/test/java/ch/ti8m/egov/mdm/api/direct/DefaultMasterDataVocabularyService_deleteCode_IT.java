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
import java.util.Locale;
import java.util.NoSuchElementException;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class DefaultMasterDataVocabularyService_deleteCode_IT extends PermissionContext {

    private static final String VOCABULARY_CODE = "VOCA_CODE";
    private static final String CODE = "CODE";

    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private MasterDataVocabularyService vocabularyService;
    private long vocabularyId;
    private long nonModifiableVocabularyId;
    @Autowired
    private MasterDataGenericEntityRepository MasterDataGenericEntityRepository;

    @BeforeEach
    public void setUp() {
        vocabularyRepository.deleteAllWithTx();

        vocabularyId = vocabularyRepository.saveWithTx(Vocabulary.builder()
                .code(DefaultMasterDataVocabularyService_deleteCode_IT.VOCABULARY_CODE)
                .modifiable(true)
                .build());
        nonModifiableVocabularyId = vocabularyRepository.saveWithTx(Vocabulary.builder()
                .code(DefaultMasterDataVocabularyService_deleteCode_IT.VOCABULARY_CODE + "_other")
                .modifiable(false)
                .build());
    }

    @Test
    void deleteExistingCode_deleteCode_codeIsArchived() {
        final long codeId = MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_deleteCode_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_deleteCode_IT.VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build());
        vocabularyService.deleteCode(codeId, vocabularyId);
        Assertions.assertWith(MasterDataGenericEntityRepository.findBy(MasterDataGenericEntity.Fields.code, DefaultMasterDataVocabularyService_deleteCode_IT.CODE), codes -> {
            Assertions.assertThat(codes).hasSize(1);
            codes.forEach(code -> Assertions.assertThat(code.isArchived()).isTrue());
        });
    }

    @Test
    void deleteExistingCodeWithMultipleLocalizations_deleteCode_codeIsArchived() {
        final long codeId = MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_deleteCode_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_deleteCode_IT.VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build());
        MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_deleteCode_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_deleteCode_IT.VOCABULARY_CODE)
                .languageCode(Locale.FRENCH.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(false)
                .build());
        vocabularyService.deleteCode(codeId, vocabularyId);
        Assertions.assertWith(MasterDataGenericEntityRepository.findBy(MasterDataGenericEntity.Fields.code, DefaultMasterDataVocabularyService_deleteCode_IT.CODE), codes -> {
            Assertions.assertThat(codes).hasSize(2);
            codes.forEach(code -> Assertions.assertThat(code.isArchived()).isTrue());
        });
    }

    @Test
    void deleteAlreadyArchivedCode_deleteCode_nothingChanges() {
        final long codeId = MasterDataGenericEntityRepository.saveWithTx(MasterDataGenericEntity.builder()
                .code(DefaultMasterDataVocabularyService_deleteCode_IT.CODE)
                .vocabularyCode(DefaultMasterDataVocabularyService_deleteCode_IT.VOCABULARY_CODE)
                .languageCode(Locale.GERMAN.getLanguage())
                .weight(1)
                .validFrom(LocalDateTime.of(2020, 5, 20, 15, 0, 0))
                .validTo(LocalDateTime.of(2150, 8, 20, 15, 0, 0))
                .shortName("my short name")
                .longName("my long name")
                .archived(true)
                .build());
        vocabularyService.deleteCode(codeId, vocabularyId);
        Assertions.assertWith(MasterDataGenericEntityRepository.findBy(MasterDataGenericEntity.Fields.code, DefaultMasterDataVocabularyService_deleteCode_IT.CODE), codes -> {
            Assertions.assertThat(codes).hasSize(1);
            codes.forEach(code -> Assertions.assertThat(code.isArchived()).isTrue());
        });
    }

    @Test
    void deleteNonExistingCode_deleteCode_noExceptionIsThrown() {
        try {
            vocabularyService.deleteCode(-1, vocabularyId);
        } catch (final Exception e) {
            Assertions.fail("Unexpected Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void deleteFromUnmodifiableVocabulary_deleteCode_exceptionIsThrown() {
        Assertions.assertThatThrownBy(() -> vocabularyService.deleteCode(0, nonModifiableVocabularyId)).isInstanceOf(NoSuchElementException.class);
    }

}
