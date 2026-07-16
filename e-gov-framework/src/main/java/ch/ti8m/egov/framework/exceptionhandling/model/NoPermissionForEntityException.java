package ch.ti8m.egov.framework.exceptionhandling.model;

public class NoPermissionForEntityException extends EGovException {

    public NoPermissionForEntityException(
            final String tableName,
            final String operation,
            final String userId
    ) {
        super(
                ExceptionCode.NO_PERMISSION_FOR_ENTITY,
                "No permission for entity",
                "entity", tableName,
                "operation", operation,
                "userId", userId
        );
    }
}
