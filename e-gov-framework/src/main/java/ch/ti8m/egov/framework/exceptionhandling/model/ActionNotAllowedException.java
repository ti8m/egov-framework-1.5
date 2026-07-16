package ch.ti8m.egov.framework.exceptionhandling.model;

import java.util.Map;

public class ActionNotAllowedException extends EGovException {

    public ActionNotAllowedException(
            final String action,
            final Enum<?> aggregateState,
            final String userId
    ) {
        super(
                ExceptionCode.ACTION_NOT_ALLOWED,
                "Action not allowed",
                Map.of(
                        "action", action,
                        "aggregateState", aggregateState == null ? "" : aggregateState,
                        "userId", userId
                )
        );
    }

}
