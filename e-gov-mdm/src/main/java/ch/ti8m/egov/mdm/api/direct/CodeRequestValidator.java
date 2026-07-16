package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.mdm.api.types.Code;
import ch.ti8m.egov.mdm.api.types.TermTranslation;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.enumerations.NameValidationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CodeRequestValidator {

    private static final String ERROR_CODE_ALREADY_EXISTS = "'code' already exists for the vocabulary";
    private static final String ERROR_MESSAGE_VALID_TO_NOT_SET = "'validTo' value must be set";
    private static final String ERROR_MESSAGE_VALID_TO_PAST = "'validTo' must not be a past value";
    private static final String ERROR_MISSING_CURRENT_VALUE = "Missing 'currentValue' field";
    private static final String ERROR_MISSING_FIELDS = "Missing 'fields' field";
    private static final String ERROR_MSG_FUTURE_MUST_MATCH_CURRENT = "'futureValue' must match 'currentValue'";
    private static final String ERROR_MSG_VALUE_TOO_LARGE = "'value' too large";
    private static final long TEXT_SIZE_LONG = 2000L;
    private static final long TEXT_SIZE_MEDIUM = 200L;

    private final MasterDataGenericEntityRepository MasterDataGenericEntityRepository;
    private final VocabularyRepository vocabularyRepository;

    public void validateCodeRequest(final Code code, final NameValidationType nameValidationType) {
        final Map<String, TermTranslation> currentValue = code.getCurrentValue();
        final Map<String, String> fields = code.getFields();
        final Map<String, TermTranslation> futureValue = code.getFutureValue();
        final LocalDate validTo = code.getValidTo();

        assert currentValue != null : ERROR_MISSING_CURRENT_VALUE;
        assert fields != null : ERROR_MISSING_FIELDS;

        validateTranslations(currentValue, nameValidationType);
        validateTranslations(futureValue, nameValidationType);

        validateFieldsSize(fields);

        assert validTo != null || futureValue == null : ERROR_MESSAGE_VALID_TO_NOT_SET;
        assert validTo == null || !validTo.isBefore(LocalDate.now()) : ERROR_MESSAGE_VALID_TO_PAST;

        validateFutureValueLanguages(currentValue, futureValue);
    }

    private void validateTranslations(Map<String, TermTranslation> translations, final NameValidationType nameValidationType) {
        if (translations != null && nameValidationType != null) {
            translations.forEach((language, translation) -> validateTranslation(translation, nameValidationType));
        }
    }

    private void validateFieldsSize(Map<String, String> fields) {
        fields.forEach((fieldName, fieldValue) -> {
            assert fieldValue.length() <= TEXT_SIZE_LONG : ERROR_MSG_VALUE_TOO_LARGE;
        });
    }

    private void validateFutureValueLanguages(Map<String, TermTranslation> currentValue, Map<String, TermTranslation> futureValue) {
        if (futureValue != null) {
            currentValue.forEach((language, translation) -> {
                assert futureValue.containsKey(language) && futureValue.get(language) != null : ERROR_MSG_FUTURE_MUST_MATCH_CURRENT;
            });
        }
    }

    public void validateCodeDuplicity(final long vocabularyId, final String code) {
        MasterDataGenericEntityRepository
                .findOneBy(
                        MasterDataGenericEntity.Fields.vocabularyCode,
                        vocabularyRepository.findById(vocabularyId).orElseThrow().getCode(),
                        MasterDataGenericEntity.Fields.code, code
                ).ifPresent(MasterDataGenericEntity -> {
                    throw new AssertionError(ERROR_CODE_ALREADY_EXISTS);
                });
    }

    private void validateTranslation(final TermTranslation translation, final NameValidationType nameValidationType) {
        if (NameValidationType.LONG_AND_SHORT_NAME.equals(nameValidationType) || NameValidationType.SHORT_NAME_ONLY.equals(nameValidationType)) {
            assert translation.getShortName() != null : "shortName must not be null";
            final int length = translation.getShortName().length();
            if (length > TEXT_SIZE_MEDIUM) {
                final String message = String.format("shortName length '%s' is too large. A length of %s is allowed", length, TEXT_SIZE_MEDIUM);
                throw new AssertionError(message);
            }
        }
        if (NameValidationType.LONG_AND_SHORT_NAME.equals(nameValidationType) || NameValidationType.LONG_NAME_ONLY.equals(nameValidationType)) {
            assert translation.getLongName() != null : "longName must not be null";
            final int length = translation.getLongName().length();
            if (length > TEXT_SIZE_LONG) {
                final String message = String.format("longName length '%s' is too large. A length of %s is allowed", length, TEXT_SIZE_LONG);
                throw new AssertionError(message);
            }
        }
    }

}
