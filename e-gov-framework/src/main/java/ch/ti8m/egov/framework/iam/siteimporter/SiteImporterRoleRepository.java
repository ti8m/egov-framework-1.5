package ch.ti8m.egov.framework.iam.siteimporter;

import ch.ti8m.egov.framework.iam.persistence.model.role.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class SiteImporterRoleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void deleteAll() {
        entityManager.createNativeQuery("DELETE FROM EGOV_IAM_Role")
                .executeUpdate();
    }

    public Role findByName(final String name) {
        final Query query = entityManager.createNativeQuery("SELECT * FROM EGOV_IAM_Role WHERE RoleName = :roleName", Role.class);
        query.setParameter("roleName", name);
        final Optional<Role> optionalFamily = query.getResultList().stream().findFirst();
        if (optionalFamily.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("Role not found: " + name);
            }
            return null;
        } else {
            return optionalFamily.get();
        }
    }

}
