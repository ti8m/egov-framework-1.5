package ch.ti8m.egov.framework.validation.engine;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@Slf4j
@Component
public class TypeCastComponent {

    private final TypeReaderComponent typeReaderComponent;
    private final SimpleDateFormat FORMAT_ISO_8601_DATE;
    String ISO_8601_DATE_FORMAT = "yyyy-MM-dd";

    @Autowired
    public TypeCastComponent(final TypeReaderComponent typeReaderComponent) {
        this.typeReaderComponent = typeReaderComponent;
        FORMAT_ISO_8601_DATE = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
        FORMAT_ISO_8601_DATE.setTimeZone(TimeZone.getDefault());
    }

    public ValidationTriple castPrimitive(
            final ValidationEntity validationEntity,
            final Object subEntity
    ) {
        final Class<?> type = typeReaderComponent.getType(validationEntity.getType());
        try {
            final Object result;
            if (subEntity instanceof LocalDate && type.equals(LocalDate.class)) {
                result = ((LocalDate) subEntity).format(DateTimeFormatter.ISO_DATE);
            } else if (subEntity instanceof LocalDateTime && type.equals(LocalDateTime.class)) {
                result = ((LocalDateTime) subEntity).format(DateTimeFormatter.ISO_DATE_TIME);
            } else if (type.equals(String.class) && subEntity instanceof Date) {
                result = toIsoDateString((Date) subEntity);
            } else if (type.equals(Date.class) && subEntity instanceof String) {
                if (subEntity.equals("")) {
                    result = null;
                } else {
                    result = fromIsoDateString((String) subEntity);
                }
            } else if (type.equals(String.class) && subEntity instanceof LocalDate) {
                result = ((LocalDate) subEntity).toString();
            } else if (type.equals(LocalDate.class) && subEntity instanceof String) {
                if (subEntity.equals("")) {
                    result = null;
                } else {
                    result = LocalDate.parse((String) subEntity);
                }
            } else if (type.equals(String.class) && subEntity instanceof LocalTime) {
                result = ((LocalTime) subEntity).toString();
            } else if (type.equals(LocalTime.class) && subEntity instanceof String) {
                if (subEntity.equals("")) {
                    result = null;
                } else {
                    result = LocalTime.parse((String) subEntity);
                }
            } else if (type.equals(String.class) && subEntity instanceof LocalDateTime) {
                result = ((LocalDateTime) subEntity).toString();
            } else if (type.equals(LocalDateTime.class) && subEntity instanceof String) {
                if (subEntity.equals("")) {
                    result = null;
                } else {
                    result = LocalDateTime.parse((String) subEntity);
                }
            } else if (subEntity instanceof Enum) {
                result = ((Enum<?>) subEntity).name();
            } else if (type.equals(String.class) && subEntity instanceof Locale) {
                result = ((Locale) subEntity).getLanguage();
            } else {
                result = type.cast(subEntity);
            }
            return new ValidationTriple(result, Collections.emptyMap(), ValidationTriple.ValidationState.VALID);
        } catch (final Exception e) {
            return new ValidationTriple(null, Map.of("error", "cannot cast " + subEntity.getClass() + " to " + type), ValidationTriple.ValidationState.INCORRECT_FORMAT);
        }
    }

    private String toIsoDateString(final Date date) {
        return date == null ? null : FORMAT_ISO_8601_DATE.format(date);
    }

    private Date fromIsoDateString(final String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        try {
            return FORMAT_ISO_8601_DATE.parse(dateString);
        } catch (final ParseException e) {
            log.error(e.getMessage(), e);
            throw new EGovException(ExceptionCode.DEFAULT, "Failed to parse date");
        }
    }

}
