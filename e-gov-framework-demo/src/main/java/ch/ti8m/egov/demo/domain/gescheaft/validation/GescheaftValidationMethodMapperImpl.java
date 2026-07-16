package ch.ti8m.egov.demo.domain.gescheaft.validation;

import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;

import java.util.List;

public class GescheaftValidationMethodMapperImpl implements ValidationMethodMapper {

    private final GescheaftApplicationServiceValidation gescheaftApplicationServiceValidation;

    public GescheaftValidationMethodMapperImpl(final GescheaftApplicationServiceValidation gescheaftApplicationServiceValidation) {
        this.gescheaftApplicationServiceValidation = gescheaftApplicationServiceValidation;
    }

    @Override
    public boolean isValid(final String validationMethod, final List<Object> parameters) {
        // implement custom validation methods here
        return false;
    }

}