package ch.ti8m.egov.mdm;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.direct.VocabularyLanguageRepository;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class PermissionContext extends EGovMdmApplicationContext {

    protected static final Long USER_ID_VALUE = 13L;
    protected static final String USER_ID = USER_ID_VALUE.toString();
    protected static final String USER_LANGUAGE = Locale.GERMAN.getLanguage();

    @Autowired
    private MasterDataGenericEntityRepository MasterDataGenericEntityRepository;
    @Autowired
    private VocabularyRepository vocabularyRepository;
    @Autowired
    private VocabularyLanguageRepository vocabularyLanguageRepository;

    @BeforeEach
    public void deactivatePermissionsForTests() {
        MasterDataGenericEntityRepository.deactivatePermissions();
        vocabularyRepository.deactivatePermissions();
        vocabularyLanguageRepository.deactivatePermissions();
        DataHolder.setUserId(USER_ID);
    }

}
