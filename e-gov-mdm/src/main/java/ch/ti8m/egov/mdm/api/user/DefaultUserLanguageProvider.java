package ch.ti8m.egov.mdm.api.user;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@ConditionalOnMissingBean(UserLanguageProvider.class)
public class DefaultUserLanguageProvider implements UserLanguageProvider {

    @Override
    public Locale getUserLanguage(String userId) {
        return Locale.getDefault();
    }

}
