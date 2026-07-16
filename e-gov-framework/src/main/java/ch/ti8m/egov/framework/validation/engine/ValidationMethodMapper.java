package ch.ti8m.egov.framework.validation.engine;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;

import java.util.List;

public interface ValidationMethodMapper {

    default boolean isValid(final String validationMethod, final List<Object> parameters) {
        return false;
    }

    default <T extends ModifiableEntity> T getAggregate() {
        return DataHolder.getAggregate();
    }

}
