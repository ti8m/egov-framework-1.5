package ch.ti8m.egov.framework.persistence.healthcheck.checks;


import ch.ti8m.egov.framework.persistence.healthcheck.checks.repository.InfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PersistenceInfoHandler implements HealthCheck {

    private final InfoRepository infoRepository;

    @Autowired
    public PersistenceInfoHandler(InfoRepository infoRepository) {
        this.infoRepository = infoRepository;
    }

    @Override
    public Map<String, Object> runHealthCheck() {
        infoRepository.deactivatePermissions();
        final List<Map<String, String>> info = infoRepository.findAll()
                .stream()
                .map(persistenceInfo -> Map.of(
                        "createdDate", persistenceInfo.getCreatedDate() == null ? "No created date provided..." : persistenceInfo.getCreatedDate().toString(),
                        "id", persistenceInfo.getId().toString(),
                        "text", persistenceInfo.getText() == null ? "" : persistenceInfo.getText()
                ))
                .collect(Collectors.toList());

        return Map.of(INFO_TEXTS, info);
    }
}
