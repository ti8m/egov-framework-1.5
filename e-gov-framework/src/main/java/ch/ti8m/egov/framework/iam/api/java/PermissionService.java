package ch.ti8m.egov.framework.iam.api.java;


import ch.ti8m.egov.framework.iam.api.model.AllowedRecordsQuery;
import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;

public interface PermissionService {

    AllowedRecordsQuery getAllowedRecordsQuery(
            final String userId,
            final PermissionOperation operation,
            final Class<?> entityClass
    );

    AllowedRecordsQuery getAllowedRecordsQuery(
            final String userId,
            final PermissionOperation operation,
            final String tableName,
            final String idFieldName
    );

}
