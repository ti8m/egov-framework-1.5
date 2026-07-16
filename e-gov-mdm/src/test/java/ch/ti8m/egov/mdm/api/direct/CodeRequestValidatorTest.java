package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.mdm.api.types.Code;
import ch.ti8m.egov.mdm.api.types.TermTranslation;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import ch.ti8m.egov.mdm.persistence.entity.enumerations.NameValidationType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeRequestValidatorTest {

    private static final String FIELD_CODE = "FIELD_CODE";
    private static final long TEXT_SIZE_LONG = 2000L;
    private static final long TEXT_SIZE_MEDIUM = 200L;

    @Mock
    private MasterDataGenericEntityRepository MasterDataGenericEntityRepository;
    @Mock
    private VocabularyRepository vocabularyRepository;
    @InjectMocks
    private CodeRequestValidator codeRequestValidator;

    @BeforeEach
    void setUp() {
    }

    @Test
    void validRequest_validateCodeRequest_noException() {
        try {
            codeRequestValidator.validateCodeRequest(createValidCodeRequest(), createTestVocabulary().getNameValidationType());
        } catch (final Exception e) {
            fail("Unexpected exception thrown", e);
        }
    }

    @Test
    void futureValueIsNull_validateCodeRequest_noException() {
        try {
            final Code code = createValidCodeRequest();
            code.setFutureValue(null);
            codeRequestValidator.validateCodeRequest(code, createTestVocabulary().getNameValidationType());
        } catch (final Exception e) {
            Assertions.fail("Unexpected exception thrown", e);
        }
    }
    
    @Test
    void futureValueIsNotNullButLongNameIsNull_validateCodeRequest_throwsException() {
        Map<String, TermTranslation> futureValue = Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "future short name",
                        null
                )
        );
        final Code code = createValidCodeRequest();
        code.setFutureValue(futureValue);
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void futureValueIsNotNullButShortNameIsNull_validateCodeRequest_throwsException() {
        Map<String, TermTranslation> futureValue = Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        null,
                        "future long name"
                )
        );
        final Code code = createValidCodeRequest();
        code.setFutureValue(futureValue);
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void futureValueAndValidToIsNull_validateCodeRequest_noException() {
        try {
            final Code code = createValidCodeRequest();
            code.setFutureValue(null);
            code.setValidTo(null);
            codeRequestValidator.validateCodeRequest(code, createTestVocabulary().getNameValidationType());
        } catch (final Exception e) {
            Assertions.fail("Unexpected exception thrown", e);
        }
    }

    @Test
    void currentValueShortNameTooLong_validateCodeRequest_throwsException() {
        final Code code = createValidCodeRequest();
        code.getCurrentValue().get(Locale.GERMAN.getLanguage()).setShortName(" ".repeat((int) TEXT_SIZE_MEDIUM + 1));
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void currentValueLongNameTooLong_validateCodeRequest_throwsException() {
        final Code code = createValidCodeRequest();
        code.getCurrentValue().get(Locale.GERMAN.getLanguage()).setLongName(" ".repeat((int) TEXT_SIZE_LONG + 1));
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void currentValueIsNull_validateCodeRequest_throwsException() {
        final Code code = createValidCodeRequest();
        code.setCurrentValue(null);
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void fieldsIsNull_validateCodeRequest_throwsException() {
        final Code code = createValidCodeRequest();
        code.setFields(null);
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void fieldValueTooLong_validateCodeRequest_throwsException() {
        final Code code = createValidCodeRequest();
        code.setFields(Map.of(
                FIELD_CODE,
                " ".repeat((int) TEXT_SIZE_LONG + 1)
        ));
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void validToIsNullAndFutureValueIsSet_validateCodeRequest_throwsException() {
        final Code code = createValidCodeRequest();
        code.setValidTo(null);
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void validToIsInPast_validateCodeRequest_throwsException() {
        final Code code = createValidCodeRequest();
        code.setValidTo(LocalDate.now().minusDays(1));
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void futureValueHasDifferentTranslationsThanCurrentValue_validateCodeRequest_throwsException() {
        final Code code = createValidCodeRequest();
        code.setCurrentValue(Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "short name",
                        "long name"
                )
        ));
        code.setFutureValue(Map.of(
                Locale.FRENCH.getLanguage(), new TermTranslation(
                        "future short name",
                        "future long name"
                )
        ));
        assertThatThrownBy(() -> codeRequestValidator.validateCodeRequest(code,
                createTestVocabulary().getNameValidationType())).isInstanceOf(AssertionError.class);
    }

    @Test
    void codeAlreadyExists_validateCodeDuplicity_throwsException() {
        final long vocabularyId = 1L;
        final String vocabularyCode = "abc";
        final String code = "123";
        when(vocabularyRepository.findById(vocabularyId)).thenReturn(Optional.of(Vocabulary.builder().code(vocabularyCode).build()));
        when(MasterDataGenericEntityRepository.findOneBy(
                MasterDataGenericEntity.Fields.vocabularyCode, vocabularyCode,
                MasterDataGenericEntity.Fields.code, code
        )).thenReturn(Optional.of(Mockito.mock(MasterDataGenericEntity.class)));

        assertThatThrownBy(() -> codeRequestValidator.validateCodeDuplicity(vocabularyId, code)).isInstanceOf(AssertionError.class);
    }

    @Test
    void codeDoesNotExistYet_validateCodeDuplicity_noException() {
        final long vocabularyId = 1L;
        final String vocabularyCode = "abc";
        final String code = "123";
        when(vocabularyRepository.findById(vocabularyId))
                .thenReturn(Optional.of(Vocabulary.builder().code(vocabularyCode).build()));
        when(MasterDataGenericEntityRepository.findOneBy(
                MasterDataGenericEntity.Fields.vocabularyCode, vocabularyCode,
                MasterDataGenericEntity.Fields.code, code
        )).thenReturn(Optional.empty());
        try {
            codeRequestValidator.validateCodeDuplicity(vocabularyId, code);
        } catch (final Exception e) {
            Assertions.fail("Unexpected exception thrown", e);
        }
    }

    private Vocabulary createTestVocabulary() {
        return Vocabulary.builder()
                .code("testVocabulary")
                .nameValidationType(NameValidationType.LONG_AND_SHORT_NAME)
                .build();
    }

    private Code createValidCodeRequest() {
        final Code code = new Code();
        code.setValidTo(LocalDate.parse("2150-01-01"));
        code.setFields(Map.of(
                FIELD_CODE, "field value"
        ));
        code.setCurrentValue(Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "short name",
                        "long name"
                )
        ));
        code.setFutureValue(Map.of(
                Locale.GERMAN.getLanguage(), new TermTranslation(
                        "future short name",
                        "future long name"
                )
        ));
        return code;
    }
}
