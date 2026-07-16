package ch.ti8m.egov.framework.persistence.healthcheck.checks.repository;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.framework.persistence.healthcheck.model.HealthCheckTestEntity;
import org.springframework.stereotype.Repository;

@ClassType(entityClass = HealthCheckTestEntity.class)
@Repository
public class HealthCheckRepository extends BaseRepositoryImpl<HealthCheckTestEntity> {
}
