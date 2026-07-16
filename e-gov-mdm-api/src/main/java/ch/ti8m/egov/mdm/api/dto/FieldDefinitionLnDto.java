package ch.ti8m.egov.mdm.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FieldDefinitionLnDto {

    private String languageCode;
    private String name;

}
