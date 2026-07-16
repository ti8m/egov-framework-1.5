package ch.ti8m.egov.mdm.api.user;

import java.util.Locale;

public interface UserLanguageProvider {
    Locale getUserLanguage(final String userId);
}
