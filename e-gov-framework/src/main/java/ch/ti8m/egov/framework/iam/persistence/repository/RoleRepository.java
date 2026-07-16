package ch.ti8m.egov.framework.iam.persistence.repository;

import ch.ti8m.egov.framework.iam.persistence.model.role.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class RoleRepository {

    private static final String ROLE_ID = "roleId";
    private static final String ROLE_NAME = "roleName";
    private final NameTranslationProvider nameTranslationProvider;
    @PersistenceContext
    private EntityManager entityManager;

    // FIND_BY_ROLE_ID
    private String getFindByRoleId() {
        return String.format(
                "SELECT EGOV_IAM_ROLE.*"
                        + " FROM EGOV_IAM_ROLE"
                        + " WHERE EGOV_IAM_ROLE.id = :%s",
                ROLE_ID
        );
    }

    // FIND_BY_ROLE_NAME
    private String getFindByRoleName() {
        return String.format(
                "SELECT EGOV_IAM_ROLE.*"
                        + " FROM EGOV_IAM_ROLE"
                        + " WHERE EGOV_IAM_ROLE." + nameTranslationProvider.getRoleNameFieldName() + " = :%s",
                ROLE_NAME
        );
    }

    private String getFindAllRoles() {
        return "SELECT EGOV_IAM_ROLE.* FROM EGOV_IAM_ROLE";
    }

    public Optional<Role> findById(final int familyId) {
        final Query query = entityManager.createNativeQuery(getFindByRoleId(), Role.class);
        query.setParameter(ROLE_ID, familyId);
        return query.getResultList().stream().findFirst();
    }

    public List<Role> findAll() {
        final Query query = entityManager.createNativeQuery(getFindAllRoles(), Role.class);
        return query.getResultList();
    }

    @Transactional
    public void save(final Role role) {
        entityManager.merge(role);
    }

    @Transactional
    public void saveAll(final List<Role> families) {
        families.forEach(entityManager::persist);
    }

    public Optional<Role> findByName(final String familyName) {
        final Query query = entityManager.createNativeQuery(getFindByRoleName(), Role.class);
        query.setParameter(ROLE_NAME, familyName);
        return query.getResultList().stream().findFirst();
    }
}
