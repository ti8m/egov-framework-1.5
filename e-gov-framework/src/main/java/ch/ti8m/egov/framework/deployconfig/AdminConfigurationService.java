package ch.ti8m.egov.framework.deployconfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminConfigurationService {
    @Value("${egov.admin.is.activated:false}")
    private boolean adminIsActivated;

    public boolean isActivated() {
        return adminIsActivated;
    }
}
