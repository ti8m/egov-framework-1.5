package ch.ti8m.egov.framework.exceptionhandling.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ExceptionUtils {

    private static final int DEFAULT_START_OFFSET = -50;
    private static final int DEFAULT_END_OFFSET = 50;

    private ExceptionUtils() {
    }

    public static String getRootCause(final Throwable e) {
        Throwable rootCause = e;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage();
    }

    public static String getStackTrace(final Throwable e) {
        return org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(e);
    }

    public static String getLink(final UUID exceptionId) {
        return "/admin/v1/exceptions/" + exceptionId
                + "?startOffset=" + DEFAULT_START_OFFSET + "&endOffset=" + DEFAULT_END_OFFSET
                + "&date=" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}