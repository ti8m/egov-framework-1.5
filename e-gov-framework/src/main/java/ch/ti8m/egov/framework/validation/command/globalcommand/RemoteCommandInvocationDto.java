package ch.ti8m.egov.framework.validation.command.globalcommand;

import ch.ti8m.egov.framework.persistence.base.Parameters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoteCommandInvocationDto {

    private String action;
    private String aggregate;
    private Object payload;
    private String userId;
    private Parameters parameters;

}
