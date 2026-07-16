package ch.ti8m.egov.framework.persistence.rest;

import ch.ti8m.egov.framework.persistence.healthcheck.checks.HealthCheckHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController("/persistence/v1")
public class PersistenceBaseController {

    private final HealthCheckHandler healthCheckHandler;

    @Autowired
    public PersistenceBaseController(HealthCheckHandler healthCheckHandler) {
        this.healthCheckHandler = healthCheckHandler;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return healthCheckHandler.runHealthCheck();
    }

}
