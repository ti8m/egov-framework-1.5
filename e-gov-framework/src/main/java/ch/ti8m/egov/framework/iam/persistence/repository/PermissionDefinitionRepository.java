package ch.ti8m.egov.framework.iam.persistence.repository;


import ch.ti8m.egov.framework.iam.persistence.model.permission.PermissionDefinition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PermissionDefinitionRepository {

    private static final String FAMILY_ID = "familyId";
    //PERMISSION_DEFINITIONS_BY_FAMILY_ID
    private final NameTranslationProvider nameTranslationProvider;
    @PersistenceContext
    private EntityManager entityManager;

    private String getPermissionDefinitionsByFamilyId() {
        return String.format(
                "SELECT EGOV_IAM_PERMISSION_DEFINITION.*"
                        + " FROM EGOV_IAM_PERMISSION_DEFINITION"
                        + " WHERE EGOV_IAM_PERMISSION_DEFINITION." + nameTranslationProvider.getFamilyIdFieldName() + " = :%s",
                FAMILY_ID
        );
    }

    public List<PermissionDefinition> findByFamilyId(final int familyId) {
        final Query query = entityManager.createNativeQuery(
                getPermissionDefinitionsByFamilyId(),
                PermissionDefinition.class
        );
        query.setParameter(FAMILY_ID, familyId);
        return query.getResultList();
    }

}
