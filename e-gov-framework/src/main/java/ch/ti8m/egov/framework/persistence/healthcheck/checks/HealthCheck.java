package ch.ti8m.egov.framework.persistence.healthcheck.checks;

import java.util.Map;

public interface HealthCheck {

    String HEALTH_FLYWAY = "FLYWAY";
    String HEALTH_OPERATIONS = "OPERATIONS";
    String HEALTH_STORED_PROCEDURES = "STORED_PROCEDURES";
    String PERSISTENCE_INFO = "PERSISTENCE_INFO";
    String ROW_COUNT = "ROW_COUNT";
    String ENTRIES = "ENTRIES";
    String STATUS = "STATUS";
    String ERROR = "ERROR";
    String FAILURE = "FAILURE";
    String MESSAGE = "MESSAGE";
    String SUCCESS = "SUCCESS";
    String STACK_TRACE = "STACK_TRACE";
    String MIGRATIONS = "MIGRATIONS";
    String CREATE = "CREATE";
    String READ = "READ";
    String UPDATE = "UPDATE";
    String DELETE = "DELETE";
    String SKIPPED = "SKIPPED";
    String INFO_TEXTS = "INFO_TEXTS";

    Map<String, Object> runHealthCheck();

}
