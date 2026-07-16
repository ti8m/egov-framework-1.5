package ch.ti8m.egov.framework.validation.engine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationTriple {

    private Object responseObject;
    private Map<String, Object> validationResult;
    private ValidationState state;
    public enum ValidationState {
        VALID,
        INVALID,
        INCORRECT_FORMAT
    }

}
