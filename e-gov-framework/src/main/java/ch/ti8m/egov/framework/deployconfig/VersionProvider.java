package ch.ti8m.egov.framework.deployconfig;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VersionProvider {

    @Value("${egov.app.version:}")
    private String appVersion;

    private final Optional<BuildProperties> buildProperties;

    public String getVersion() {
        if (appVersion != null && !appVersion.isEmpty()) {
            return appVersion.trim();
        }
        return buildProperties
                .map(BuildProperties::getVersion)
                .map(String::trim)
                .filter(version -> !version.isEmpty())
                .orElse("n/a");
    }
}
