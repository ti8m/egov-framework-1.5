package ch.ti8m.egov.demo.domain.gescheaft.persistence;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@ClassType(entityClass = Gescheaft.class)
@Repository
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class GescheaftRepository extends BaseRepositoryImpl<Gescheaft> {

    @PostConstruct
    public void init() {
        log.warn("Permissions deactivated by domain generator for repository " + this.getClass());
        deactivatePermissionsGlobally();
    }

}