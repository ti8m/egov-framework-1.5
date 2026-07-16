package ch.ti8m.egov.mdm.api.direct;

import ch.ti8m.egov.mdm.api.types.Code;
import ch.ti8m.egov.mdm.api.types.DetailedCode;
import ch.ti8m.egov.mdm.api.types.DetailedVocabulary;
import ch.ti8m.egov.mdm.api.types.ResultCode;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unused")
public interface MasterDataVocabularyService {
    List<ResultCode> getCodesForVocabulary(long vocabularyId, Locale userLanguage);

    List<ResultCode> getCodesForVocabulary(String vocabularyCode, Locale userLanguage);

    Map<String, List<ResultCode>> getCodesForMultipleVocabularies(List<String> vocabularyCodes, Locale userLanguage);

    Map<String, String> getTranslatedTerms(String vocabularyCode, String codeName);

    DetailedVocabulary getVocabulary(long vocabularyId, Locale userLanguage);

    long createCodeForVocabulary(long vocabularyId, DetailedCode DetailedCode);

    void deleteCode(long codeId, long vocabularyId);

    void updateCodeForVocabulary(long vocabularyId, long codeId, Code code);
}
