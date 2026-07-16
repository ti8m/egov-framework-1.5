package ch.ti8m.egov.framework.iam.persistence.repository;


import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.iam.persistence.model.permission.PermissionDefinition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class PermissionRepository {

    private static final String USER_ID = "userId";
    private static final String ENTITY_TITLE = "entityTitle";
    private static final String OPERATION = "operation";

    private final DatabaseConfigurationService databaseConfigurationService;
    private NameTranslationProvider nameTranslationProvider;
    @PersistenceContext
    private EntityManager entityManager;

    private String getAllUserPermissionsString() {
        return String.format(
                "SELECT EGOV_IAM_PERMISSION_DEFINITION.*"
                        + " FROM EGOV_IAM_PERMISSION_DEFINITION"
                        + " JOIN EGOV_IAM_ROLE_MEMBERSHIP ON EGOV_IAM_ROLE_MEMBERSHIP.roleid = EGOV_IAM_PERMISSION_DEFINITION.roleid"
                        + " WHERE EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getUserIdFieldName() + " = :%1$s"
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidFromFieldName() + " <= %2$s"
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidToFieldName() + " >= %2$s",
                USER_ID,
                databaseConfigurationService.getCurrentDateString());
    }

    private String getUserPermissionForEntityString() {
        return String.format(

                "SELECT *"
                        + " FROM ("
                        + "   SELECT EGOV_IAM_PERMISSION_DEFINITION.*,"
                        + "   ROW_NUMBER() OVER(PARTITION BY EGOV_IAM_PERMISSION_DEFINITION." + nameTranslationProvider.getSqlQueryFieldName() + " ORDER BY EGOV_IAM_PERMISSION_DEFINITION.ID DESC) rowNumber"
                        + "   FROM EGOV_IAM_PERMISSION_DEFINITION"
                        + "   JOIN EGOV_IAM_ROLE_MEMBERSHIP ON EGOV_IAM_ROLE_MEMBERSHIP.roleid = EGOV_IAM_PERMISSION_DEFINITION.roleid"
                        + "   WHERE EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getUserIdFieldName() + " = :%1$s"
                        + "   AND EGOV_IAM_PERMISSION_DEFINITION." + nameTranslationProvider.getEntityTitleFieldName() + " = :%2$s"
                        + "   AND EGOV_IAM_PERMISSION_DEFINITION." + nameTranslationProvider.getOperationFieldName() + " = :%3$s"
                        + "   AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidFromFieldName() + " <= %4$s"
                        + "   AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidToFieldName() + " >= %4$s"
                        + " ) permission"
                        + " WHERE rowNumber = 1",
                USER_ID,
                ENTITY_TITLE,
                OPERATION,
                databaseConfigurationService.getCurrentDateString()
        );
    }

    public List<PermissionDefinition> getAllUserPermissions(final String userId) {
        final Query query = entityManager.createNativeQuery(getAllUserPermissionsString(), PermissionDefinition.class);
        query.setParameter(USER_ID, userId);
        return query.getResultList();
    }

    public List<PermissionDefinition> getUserPermissions(
            final String userId,
            final PermissionOperation operation,
            final String entityTitle
    ) {
        final Query query = entityManager.createNativeQuery(getUserPermissionForEntityString(),
                PermissionDefinition.class);
        query.setParameter(USER_ID, userId);
        query.setParameter(OPERATION, operation.name());
        query.setParameter(ENTITY_TITLE, entityTitle);
        return query.getResultList();
    }

}
