package ch.ti8m.egov.framework.validation.engine;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class TypeReaderComponent {

    private static final String LONG = "long";
    private static final String STRING = "string";
    private static final String INT = "int";
    private static final String BOOL = "bool";
    private static final String UUID_TYPE = "uuid";
    private static final String LOCAL_DATE = "localdate";
    private static final String LOCAL_TIME = "localtime";
    private static final String LOCAL_DATETIME = "localdatetime";

    public Class<?> getType(final String type) {
        switch (type) {
            case LONG:
                return Long.class;
            case STRING:
                return String.class;
            case INT:
                return Integer.class;
            case BOOL:
                return Boolean.class;
            case UUID_TYPE:
                return UUID.class;
            case LOCAL_DATE:
                return LocalDate.class;
            case LOCAL_TIME:
                return LocalTime.class;
            case LOCAL_DATETIME:
                return LocalDateTime.class;
            default:
                throw new ValidationException("Unexpected type: " + type);
        }
    }

}
