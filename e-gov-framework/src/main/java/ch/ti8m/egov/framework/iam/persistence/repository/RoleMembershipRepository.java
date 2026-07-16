package ch.ti8m.egov.framework.iam.persistence.repository;


import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.iam.persistence.model.role.RoleMembership;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class RoleMembershipRepository {

    private static final String ROLE_NAME = "roleName";
    private static final String USER_ID = "userId";
    private static final String ROLE_MEMBERSHIP_ID = "roleMembershipId";
    private final NameTranslationProvider nameTranslationProvider;
    private final DatabaseConfigurationService databaseConfigurationService;
    @PersistenceContext
    private EntityManager entityManager;

    // USER_MEMBER_OF_ROLE
    private String getUserMemberOfRole() {
        return String.format(
                "SELECT EGOV_IAM_ROLE_MEMBERSHIP.*"
                        + " FROM EGOV_IAM_ROLE_MEMBERSHIP"
                        + " JOIN EGOV_IAM_ROLE ON EGOV_IAM_ROLE.id = EGOV_IAM_ROLE_MEMBERSHIP.roleid"
                        + " WHERE EGOV_IAM_ROLE." + nameTranslationProvider.getRoleNameFieldName() + " = :%s"
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getUserIdFieldName() + " = :%s"
                        + " AND " + databaseConfigurationService.getCurrentDateString()
                        + " BETWEEN EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidFromFieldName()
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidToFieldName(),
                ROLE_NAME,
                USER_ID
        );
    }

    // USER_ROLES
    private String getUserRoles() {
        return String.format(
                "SELECT *"
                        + " FROM EGOV_IAM_ROLE_MEMBERSHIP"
                        + " WHERE EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getUserIdFieldName() + " = :%s"
                        + " AND " + databaseConfigurationService.getCurrentDateString()
                        + " BETWEEN EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidFromFieldName()
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidToFieldName(),
                USER_ID
        );
    }

    // ROLE_MEMBERSHIP
    private String getRoleMemberships() {
        return String.format(
                "SELECT EGOV_IAM_ROLE_MEMBERSHIP.*"
                        + " FROM EGOV_IAM_ROLE_MEMBERSHIP"
                        + " JOIN EGOV_IAM_ROLE ON EGOV_IAM_ROLE.id = EGOV_IAM_ROLE_MEMBERSHIP.roleid"
                        + " WHERE EGOV_IAM_ROLE." + nameTranslationProvider.getRoleNameFieldName() + " = :%s"
                        + " AND " + databaseConfigurationService.getCurrentDateString()
                        + " BETWEEN EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidFromFieldName()
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidToFieldName(),
                ROLE_NAME
        );
    }

    // FIND_BY_ID
    private String getFindById() {
        return String.format(
                "SELECT *"
                        + " FROM EGOV_IAM_ROLE_MEMBERSHIP"
                        + " WHERE EGOV_IAM_ROLE_MEMBERSHIP.id = :%s",
                ROLE_MEMBERSHIP_ID
        );
    }

    @Transactional
    public void save(final RoleMembership roleMembership) {
        if (roleMembership.getId() == null) {
            entityManager.persist(roleMembership);
        } else {
            entityManager.merge(roleMembership);
        }
    }

    @Transactional
    public void deleteById(final Integer roleMembershipId) {
        final Query query = entityManager.createNativeQuery(getFindById(), RoleMembership.class);
        query.setParameter(ROLE_MEMBERSHIP_ID, roleMembershipId);
        final RoleMembership roleMembership = (RoleMembership) query.getResultList()
                .stream()
                .findFirst()
                .get();

        entityManager.remove(roleMembership);
    }

    @Transactional
    public void terminateForUserId(final String userId, final String roleName) {
        final Query query = entityManager.createNativeQuery(getUserMemberOfRole(), RoleMembership.class);
        query.setParameter(USER_ID, userId);
        query.setParameter(ROLE_NAME, roleName);
        final List<RoleMembership> roleMemberships = (List<RoleMembership>) query.getResultList();
        roleMemberships.forEach(roleMembership -> {
            roleMembership.setValidTo(LocalDateTime.now().minusSeconds(1L));
            entityManager.merge(roleMembership);
        });
    }

    public List<RoleMembership> getMembershipsOfRole(final String roleName) {
        final Query query = entityManager.createNativeQuery(getRoleMemberships(), RoleMembership.class);
        query.setParameter(ROLE_NAME, roleName);
        return query.getResultList();
    }

    public boolean userIsMemberOfRole(
            final String userId,
            final String roleName
    ) {
        final Query query = entityManager.createNativeQuery(getUserMemberOfRole(), RoleMembership.class);
        query.setParameter(USER_ID, userId);
        query.setParameter(ROLE_NAME, roleName);
        return !query.getResultList().isEmpty();
    }

    public List<RoleMembership> getRolesByUserId(final String userId) {
        final Query query = entityManager.createNativeQuery(getUserRoles(), RoleMembership.class);
        query.setParameter(USER_ID, userId);
        return query.getResultList();
    }
}
