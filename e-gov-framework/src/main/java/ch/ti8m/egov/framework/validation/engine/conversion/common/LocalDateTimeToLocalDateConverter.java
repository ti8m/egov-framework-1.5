package ch.ti8m.egov.framework.validation.engine.conversion.common;

import ch.ti8m.egov.framework.validation.engine.conversion.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class LocalDateTimeToLocalDateConverter extends Converter {

    @Override
    protected LocalDate convert(final Object input) {
        return input == null ? null : ((LocalDateTime) input).toLocalDate();
    }
}
