package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.mdm.api.types.Code;
import ch.ti8m.egov.mdm.api.types.DetailedCode;
import ch.ti8m.egov.mdm.api.types.DetailedVocabulary;
import ch.ti8m.egov.mdm.api.types.Field;
import ch.ti8m.egov.mdm.api.types.ResultCode;
import ch.ti8m.egov.mdm.api.types.SimplifiedCode;
import ch.ti8m.egov.mdm.persistence.entity.LanguageDefinition;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntityFactory;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import ch.ti8m.egov.mdm.persistence.entity.VocabularyLn;
import ch.ti8m.egov.mdm.persistence.entity.enumerations.NameValidationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultMasterDataVocabularyService implements MasterDataVocabularyService {

    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);
    private final MasterDataGenericEntityRepository masterDataGenericEntityRepository;
    private final VocabularyRepository vocabularyRepository;
    private final CodeRequestValidator codeRequestValidator;

    @Override
    public List<ResultCode> getCodesForVocabulary(final String vocabularyCode, final Locale userLanguage) {
        return getCodesForVocabulary(vocabularyCode, null, userLanguage);
    }

    public List<ResultCode> getCodesForVocabulary(final String vocabularyCode, final String filter, final Locale userLanguage) {
        final Long vocabularyId = vocabularyRepository.findOneBy(ch.ti8m.egov.mdm.persistence.entity.Vocabulary.Fields.code, vocabularyCode)
                .map(ch.ti8m.egov.mdm.persistence.entity.Vocabulary::getId)
                .orElseThrow();

        return getSimpleCodesWithCurrentTerms(vocabularyId, userLanguage, filter);
    }

    @Override
    public Map<String, String> getTranslatedTerms(final String vocabularyCode, final String codeName) {
        return findByMachineNameAndCode(vocabularyCode, codeName)
                .stream()
                .collect(Collectors.toMap(
                        MasterDataGenericEntity::getLanguageCode,
                        MasterDataGenericEntity::getLongName
                ));
    }

    private List<MasterDataGenericEntity> findByMachineNameAndCode(final String vocabularyCode, final String code) {
        final LocalDateTime now = LocalDateTime.now();
        return masterDataGenericEntityRepository.findWithFilter(
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + vocabularyCode + "'",
                MasterDataGenericEntity.Fields.code + " == '" + code + "'",
                MasterDataGenericEntity.Fields.validFrom + " <= " + now,
                MasterDataGenericEntity.Fields.validTo + " >= " + now
        );
    }

    @Override
    public DetailedVocabulary getVocabulary(final long vocabularyId, final Locale userLanguage) {
        final ch.ti8m.egov.mdm.persistence.entity.Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId).orElseThrow();
        return DetailedVocabulary.builder()
                .languages(getVocabularyLanguages(vocabulary))
                .fields(getVocabularyFields(vocabulary, userLanguage))
                .codes(getActiveCodesForLanguage(userLanguage, vocabulary.getCode()).stream()
                        .map(SimplifiedCode::fromMasterDataGenericEntity)
                        .toList())
                .vocabularyId(vocabularyId)
                .shortName(vocabulary.getLocalization(userLanguage.getLanguage()).map(VocabularyLn::getName).orElse(null))
                .longName(vocabulary.getLocalization(userLanguage.getLanguage()).map(VocabularyLn::getDescription).orElse(null))
                .build();
    }

    private List<MasterDataGenericEntity> getActiveCodesForLanguage(final Locale userLanguage, final String vocabularyCode) {
        return getActiveCodesForLanguage(userLanguage, vocabularyCode, null);
    }

    private List<MasterDataGenericEntity> getActiveCodesForLanguage(final Locale userLanguage, final String vocabularyCode, final String filter) {
        final LocalDateTime now = LocalDateTime.now();
        return masterDataGenericEntityRepository.findWithFilter(
                MasterDataGenericEntity.Fields.vocabularyCode + " == '" + vocabularyCode + "'",
                MasterDataGenericEntity.Fields.validFrom + " <= " + now,
                MasterDataGenericEntity.Fields.validTo + " >= " + now,
                MasterDataGenericEntity.Fields.languageCode + " == '" + userLanguage + "'",
                filter == null ? null : MasterDataGenericEntity.Fields.additionalContent + " LIKE '%" + filter + "%'"
        );
    }

    private List<Field> getVocabularyFields(final ch.ti8m.egov.mdm.persistence.entity.Vocabulary vocabulary, final Locale userLanguage) {
        return vocabulary.getFields()
                .stream()
                .map(fieldDefinition -> Field.fromField(fieldDefinition, userLanguage))
                .toList();
    }

    private List<String> getVocabularyLanguages(final ch.ti8m.egov.mdm.persistence.entity.Vocabulary vocabulary) {
        return vocabulary.getLanguages()
                .stream()
                .map(LanguageDefinition::getLanguageCode)
                .toList();
    }

    @Override
    public List<ResultCode> getCodesForVocabulary(final long vocabularyId, final Locale userLanguage) {
        return getSimpleCodesWithCurrentTerms(vocabularyId, userLanguage);
    }

    @Override
    public Map<String, List<ResultCode>> getCodesForMultipleVocabularies(final List<String> vocabularyCodes, final Locale userLanguage) {
        return vocabularyCodes.stream()
                .map(vocabularyCode -> new AbstractMap.SimpleEntry<>(
                        vocabularyCode,
                        getCodesForVocabulary(vocabularyCode, userLanguage))
                )
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue)
                );
    }

    @Override
    public void deleteCode(final long codeId, final long vocabularyId) {
        // archived all correlated records in a vocabulary, including archived, outdated and future records
        final ch.ti8m.egov.mdm.persistence.entity.Vocabulary vocabulary = vocabularyRepository.findOneBy(
                ch.ti8m.egov.mdm.persistence.entity.Vocabulary.Fields.id, vocabularyId,
                ch.ti8m.egov.mdm.persistence.entity.Vocabulary.Fields.modifiable, true
        ).orElseThrow();

        masterDataGenericEntityRepository
                .findOneBy(
                        MasterDataGenericEntity.Fields.vocabularyCode, vocabulary.getCode(),
                        MasterDataGenericEntity.Fields.id, codeId)
                .ifPresent(code -> masterDataGenericEntityRepository.findBy(
                        MasterDataGenericEntity.Fields.vocabularyCode, vocabulary.getCode(),
                        MasterDataGenericEntity.Fields.code,
                        code.getCode()).forEach(localizedCode -> {
                    localizedCode.setArchived(true);
                    masterDataGenericEntityRepository.updateWithTx(localizedCode);
                }));
    }

    @Override
    public long createCodeForVocabulary(final long vocabularyId, final DetailedCode detailedCode) {
        Optional<Vocabulary> optionalVocabulary = vocabularyRepository.findById(vocabularyId);
        final String vocabularyCode = optionalVocabulary.orElseThrow().getCode();
        final NameValidationType nameValidationType = optionalVocabulary.orElseThrow().getNameValidationType();
        codeRequestValidator.validateCodeRequest(detailedCode, nameValidationType);
        codeRequestValidator.validateCodeDuplicity(vocabularyId, detailedCode.getCode());
        createCurrentCodes(vocabularyCode, detailedCode);
        createFutureCodes(vocabularyCode, detailedCode);
        return masterDataGenericEntityRepository.findOneBy(
                MasterDataGenericEntity.Fields.vocabularyCode, vocabularyCode,
                MasterDataGenericEntity.Fields.code, detailedCode.getCode()
        ).orElseThrow().getId();
    }

    private void createCurrentCodes(final String vocabularyCode, final DetailedCode detailedCode) {
        final List<MasterDataGenericEntity> currentCodes = detailedCode.getCurrentValue().entrySet()
                .stream()
                .map(requestEntry -> MasterDataGenericEntityFactory.fromDetailedCode(
                        vocabularyCode,
                        detailedCode,
                        requestEntry.getKey(),
                        requestEntry.getValue(),
                        LocalDate.now().atStartOfDay(),
                        detailedCode.getValidTo() == null ? DefaultMasterDataVocabularyService.MAX_DATE.atStartOfDay() : detailedCode.getValidTo().plusDays(1).atStartOfDay()
                ))
                .toList();
        masterDataGenericEntityRepository.save(currentCodes);
    }

    private void createFutureCodes(final String vocabularyCode, final DetailedCode detailedCode) {
        if (detailedCode.getFutureValue() != null) {
            final List<MasterDataGenericEntity> futureCodes = detailedCode.getFutureValue().entrySet()
                    .stream()
                    .map(requestEntry -> MasterDataGenericEntityFactory.fromDetailedCode(
                            vocabularyCode,
                            detailedCode,
                            requestEntry.getKey(),
                            requestEntry.getValue(),
                            detailedCode.getValidTo().plusDays(1).atStartOfDay(),
                            DefaultMasterDataVocabularyService.MAX_DATE.atStartOfDay()
                    ))
                    .toList();
            masterDataGenericEntityRepository.save(futureCodes);
        }
    }

    @Override
    public void updateCodeForVocabulary(final long vocabularyId, final long codeId, final Code code) {
        final Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId).orElseThrow();
        codeRequestValidator.validateCodeRequest(code, vocabulary.getNameValidationType());
        final MasterDataGenericEntity referencedCode = masterDataGenericEntityRepository.findOneBy(
                MasterDataGenericEntity.Fields.id, codeId,
                MasterDataGenericEntity.Fields.vocabularyCode, vocabulary.getCode()
        ).orElseThrow();
        final List<MasterDataGenericEntity> codes = masterDataGenericEntityRepository.findBy(
                MasterDataGenericEntity.Fields.code, referencedCode.getCode(),
                MasterDataGenericEntity.Fields.vocabularyCode, vocabulary.getCode()
        );

        updateFields(codes, code);
        deleteAllFutureVersions(codes);
        expireCurrentTerms(referencedCode.getCode(), vocabulary.getCode());
        createNewTermVersions(referencedCode.getCode(), vocabulary.getCode(), code);
        createFutureTermVersion(referencedCode.getCode(), vocabulary.getCode(), code);
    }

    private void expireCurrentTerms(final String code, final String vocabularyCode) {
        final List<MasterDataGenericEntity> currentlyActiveLocalizations = findByMachineNameAndCode(vocabularyCode, code);
        final LocalDateTime updatedValidFrom = LocalDate.now().atStartOfDay();
        currentlyActiveLocalizations.forEach(currentlyActiveLocalization -> currentlyActiveLocalization.setValidTo(updatedValidFrom));
        masterDataGenericEntityRepository.update(currentlyActiveLocalizations);
    }

    private void createNewTermVersions(final String code, final String vocabularyCode, final Code codeRequest) {
        final LocalDateTime today = LocalDate.now().atStartOfDay();
        final List<MasterDataGenericEntity> newVersions = codeRequest.getCurrentValue()
                .entrySet()
                .stream()
                .map(requestEntry -> MasterDataGenericEntityFactory.fromCode(
                        vocabularyCode,
                        code,
                        codeRequest.getFields(),
                        requestEntry.getKey(),
                        requestEntry.getValue(),
                        today,
                        codeRequest.getValidTo() == null ? DefaultMasterDataVocabularyService.MAX_DATE.atStartOfDay() : codeRequest.getValidTo().plusDays(1).atStartOfDay()
                ))
                .toList();
        masterDataGenericEntityRepository.saveWithTx(newVersions);
    }

    private void createFutureTermVersion(final String code, final String vocabularyCode, final Code codeRequest) {
        if (codeRequest.getFutureValue() == null) {
            return;
        }

        final List<MasterDataGenericEntity> newVersions = codeRequest.getFutureValue()
                .entrySet()
                .stream()
                .map(requestEntry -> MasterDataGenericEntityFactory.fromCode(
                        vocabularyCode,
                        code,
                        codeRequest.getFields(),
                        requestEntry.getKey(),
                        requestEntry.getValue(),
                        codeRequest.getValidTo().plusDays(1).atStartOfDay(),
                        DefaultMasterDataVocabularyService.MAX_DATE.atStartOfDay()
                )).toList();

        masterDataGenericEntityRepository.saveWithTx(newVersions);
    }

    private void deleteAllFutureVersions(final List<MasterDataGenericEntity> codes) {
        final LocalDateTime now = LocalDateTime.now();
        masterDataGenericEntityRepository.deleteWithTx(codes.stream()
                .filter(code -> code.getValidFrom().isAfter(now))
                .toList()
        );
    }

    private void updateFields(final List<MasterDataGenericEntity> codes, final Code codeRequest) {
        codes.forEach(code -> codeRequest.getFields().forEach((fieldCode, fieldValue) -> code.getAdditionalContent().put(fieldCode, fieldValue)));
        masterDataGenericEntityRepository.update(codes);
    }

    private List<ResultCode> getSimpleCodesWithCurrentTerms(final Long vocabularyId, final Locale userLanguage) {
        return getSimpleCodesWithCurrentTerms(vocabularyId, userLanguage, null);
    }

    private List<ResultCode> getSimpleCodesWithCurrentTerms(final Long vocabularyId, final Locale userLanguage, final String filter) {
        return getActiveCodesForLanguage(userLanguage, vocabularyRepository.findById(vocabularyId).orElseThrow().getCode(), filter)
                .stream()
                .map(ResultCode::fromEntity)
                .toList();
    }

}
