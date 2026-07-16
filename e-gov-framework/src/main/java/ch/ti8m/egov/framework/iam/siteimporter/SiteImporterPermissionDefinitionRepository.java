package ch.ti8m.egov.framework.iam.siteimporter;

import ch.ti8m.egov.framework.iam.persistence.model.permission.PermissionDefinition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SiteImporterPermissionDefinitionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveAll(final List<List<PermissionDefinition>> permissionDefinitions) {
        permissionDefinitions.forEach(permissionDefinitionsSub -> permissionDefinitionsSub.forEach(entityManager::merge));
    }

    public void deleteAll() {
        entityManager.createNativeQuery("DELETE FROM EGOV_IAM_PermissionDefinition")
                .executeUpdate();
    }
}
