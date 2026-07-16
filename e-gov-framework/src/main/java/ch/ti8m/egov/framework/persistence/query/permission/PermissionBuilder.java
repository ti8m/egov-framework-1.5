package ch.ti8m.egov.framework.persistence.query.permission;

import ch.ti8m.egov.framework.iam.api.java.PermissionService;
import ch.ti8m.egov.framework.iam.api.model.AllowedRecordsQuery;
import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.framework.persistence.query.ParametrizedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PermissionBuilder {

    private final PermissionService permissionService;

    public PermissionBuilder(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public <T extends ModifiableEntity> ParametrizedQuery getPermissions(
            final String userId,
            final Class<T> clazz,
            final PermissionOperation operation
    ) {
        final AllowedRecordsQuery allowedRecordsQuery = permissionService.getAllowedRecordsQuery(
                userId,
                operation,
                clazz
        );
        return new ParametrizedQuery(
                allowedRecordsQuery.getQuery(),
                allowedRecordsQuery.getValues()
        );
    }

    public ParametrizedQuery getPermissions(
            final String userId,
            final String tableName,
            final String idFieldName,
            final PermissionOperation operation
    ) {
        final AllowedRecordsQuery allowedRecordsQuery = permissionService.getAllowedRecordsQuery(
                userId,
                operation,
                tableName,
                idFieldName
        );
        return new ParametrizedQuery(
                allowedRecordsQuery.getQuery(),
                allowedRecordsQuery.getValues()
        );
    }

}
