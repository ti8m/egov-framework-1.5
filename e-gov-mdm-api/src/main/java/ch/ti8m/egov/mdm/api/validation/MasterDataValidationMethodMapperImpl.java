package ch.ti8m.egov.mdm.api.validation;

import ch.ti8m.egov.framework.validation.engine.ValidationMethodMapper;

public class MasterDataValidationMethodMapperImpl implements ValidationMethodMapper {

    private final MasterDataApplicationServiceValidation masterDataApplicationServiceValidation;

    public MasterDataValidationMethodMapperImpl(final MasterDataApplicationServiceValidation masterDataApplicationServiceValidation) {
        this.masterDataApplicationServiceValidation = masterDataApplicationServiceValidation;
    }

}
