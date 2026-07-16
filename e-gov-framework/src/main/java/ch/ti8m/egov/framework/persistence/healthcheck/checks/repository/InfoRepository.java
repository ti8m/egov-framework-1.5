package ch.ti8m.egov.framework.persistence.healthcheck.checks.repository;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.framework.persistence.healthcheck.model.PersistenceInfo;
import org.springframework.stereotype.Repository;

@ClassType(entityClass = PersistenceInfo.class)
@Repository
public class InfoRepository extends BaseRepositoryImpl<PersistenceInfo> {
}
