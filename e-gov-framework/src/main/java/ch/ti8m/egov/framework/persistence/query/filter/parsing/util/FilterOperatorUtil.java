package ch.ti8m.egov.framework.persistence.query.filter.parsing.util;


import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;

import java.util.List;

public final class FilterOperatorUtil {

    private static final List<String> ALLOWED_OPERATIONS =
            List.of(">", "<", "==", "<=", ">=", "!=", "IS", "LIKE", "NOT LIKE", "IN", "NOT IN", "CONTAINS", "IS NOT");

    private FilterOperatorUtil() {
    }

    public static String getOperator(final String requestedOperator) {
        if (ALLOWED_OPERATIONS.contains(requestedOperator)) {
            if (requestedOperator.equals("==")) {
                return "=";
            }
            return requestedOperator;
        }
        throw new EGovException(ExceptionCode.FILTER_OPERATION_NOT_PERMITTED, "filter operator :" + requestedOperator + " not permitted");
    }

}
