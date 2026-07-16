package ch.ti8m.egov.mdm.api.abstraction;

import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor
@Data
public abstract class Masterdata {
    private String code;
    private String languageCode;
    private int weight;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String shortName;
    private String longName;
    private boolean archived;
    @JsonIgnore
    private MasterDataGenericEntity MasterDataGenericEntity;
}
