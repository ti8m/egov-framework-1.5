package ch.ti8m.egov.mdm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FieldDefinitionDto {

    private String code;
    private String type;
    private String defaultValue;
    private List<FieldDefinitionLnDto> localizations;

}
