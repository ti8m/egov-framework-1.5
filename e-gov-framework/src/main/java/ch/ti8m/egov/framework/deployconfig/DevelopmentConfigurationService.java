package ch.ti8m.egov.framework.deployconfig;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DevelopmentConfigurationService {
    @Value("${egov.permission.log.activated:false}")
    private boolean permissionLogActivated;
}
