package ch.ti8m.egov.framework.persistence.healthcheck.checks;


import ch.ti8m.egov.framework.exceptionhandling.utils.ExceptionUtils;
import ch.ti8m.egov.framework.persistence.healthcheck.checks.repository.HealthCheckRepository;
import ch.ti8m.egov.framework.persistence.healthcheck.model.HealthCheckTestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class OperationCheckHandler implements HealthCheck {

    private final HealthCheckRepository healthCheckRepository;

    @Autowired
    public OperationCheckHandler(final HealthCheckRepository healthCheckRepository) {
        this.healthCheckRepository = healthCheckRepository;
    }

    @Override
    public Map<String, Object> runHealthCheck() {
        try {
            healthCheckRepository.deactivatePermissions();
            final Map.Entry<Long, Map<String, Object>> createHealth = getCreateHealth(healthCheckRepository);
            return Map.of(
                    HealthCheck.CREATE, createHealth.getValue(),
                    HealthCheck.READ, createHealth.getKey() == null ? HealthCheck.SKIPPED : getReadHealth(createHealth.getKey(), healthCheckRepository),
                    HealthCheck.UPDATE, createHealth.getKey() == null ? HealthCheck.SKIPPED : getUpdateHealth(createHealth.getKey(), healthCheckRepository),
                    HealthCheck.DELETE, createHealth.getKey() == null ? HealthCheck.SKIPPED : getDeleteHealth(createHealth.getKey(), healthCheckRepository)
            );
        } catch (final Exception e) {
            return Map.of(
                    HealthCheck.STATUS, HealthCheck.ERROR,
                    HealthCheck.MESSAGE, e.getMessage() == null ? "" : e.getMessage(),
                    HealthCheck.STACK_TRACE, e.getStackTrace() == null ? "" : ExceptionUtils.getStackTrace(e)
            );
        }
    }

    private Map.Entry<Long, Map<String, Object>> getCreateHealth(final HealthCheckRepository healthCheckRepository) {
        try {
            final HealthCheckTestEntity healthCheckTestEntity = new HealthCheckTestEntity();
            healthCheckTestEntity.setText(UUID.randomUUID().toString());
            healthCheckRepository.saveWithTx(healthCheckTestEntity);
            return new AbstractMap.SimpleEntry<>(
                    healthCheckTestEntity.getId(),
                    Map.of(HealthCheck.STATUS, HealthCheck.SUCCESS)
            );
        } catch (final Exception e) {
            return new AbstractMap.SimpleEntry<>(
                    null,
                    Map.of(
                            HealthCheck.STATUS, HealthCheck.ERROR,
                            HealthCheck.MESSAGE, e.getMessage() == null ? "" : e.getMessage(),
                            HealthCheck.STACK_TRACE, e.getStackTrace() == null ? "" : ExceptionUtils.getStackTrace(e)
                    )
            );
        }
    }

    private Map<String, Object> getReadHealth(final Long id, final HealthCheckRepository healthCheckRepository) {
        try {
            final Optional<HealthCheckTestEntity> healthCheckTestEntity = healthCheckRepository.findById(id);
            if (healthCheckTestEntity.isPresent()) {
                return Map.of(HealthCheck.STATUS, HealthCheck.SUCCESS);
            } else {
                return Map.of(
                        HealthCheck.STATUS, HealthCheck.FAILURE,
                        HealthCheck.MESSAGE, "Could not read TestEntity with ID '" + id + "'"
                );
            }
        } catch (final Exception e) {
            return Map.of(
                    HealthCheck.STATUS, HealthCheck.ERROR,
                    HealthCheck.MESSAGE, e.getMessage() == null ? "" : e.getMessage(),
                    HealthCheck.STACK_TRACE, e.getStackTrace() == null ? "" : ExceptionUtils.getStackTrace(e)
            );
        }
    }

    private Map<String, Object> getUpdateHealth(final Long id, final HealthCheckRepository healthCheckRepository) {
        try {
            final Optional<HealthCheckTestEntity> healthCheckTestEntity = healthCheckRepository.findById(id);
            if (healthCheckTestEntity.isPresent()) {
                healthCheckTestEntity.get().setText(UUID.randomUUID().toString());
                healthCheckRepository.updateWithTx(healthCheckTestEntity.get());
                return Map.of(HealthCheck.STATUS, HealthCheck.SUCCESS);
            } else {
                return Map.of(
                        HealthCheck.STATUS, HealthCheck.FAILURE,
                        HealthCheck.MESSAGE, "Could not read TestEntity for update with ID '" + id + "'"
                );
            }
        } catch (final Exception e) {
            return Map.of(
                    HealthCheck.STATUS, HealthCheck.ERROR,
                    HealthCheck.MESSAGE, e.getMessage() == null ? "" : e.getMessage(),
                    HealthCheck.STACK_TRACE, e.getStackTrace() == null ? "" : ExceptionUtils.getStackTrace(e)
            );
        }
    }

    private Map<String, Object> getDeleteHealth(final Long id, final HealthCheckRepository healthCheckRepository) {
        try {
            healthCheckRepository.deleteWithTx(id);
            return Map.of(HealthCheck.STATUS, HealthCheck.SUCCESS);
        } catch (final Exception e) {
            return Map.of(
                    HealthCheck.STATUS, HealthCheck.ERROR,
                    HealthCheck.MESSAGE, e.getMessage() == null ? "" : e.getMessage(),
                    HealthCheck.STACK_TRACE, e.getStackTrace() == null ? "" : ExceptionUtils.getStackTrace(e)
            );
        }
    }

}
