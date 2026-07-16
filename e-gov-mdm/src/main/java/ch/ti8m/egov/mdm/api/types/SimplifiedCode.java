package ch.ti8m.egov.mdm.api.types;

import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Getter
@Setter
@Builder
public class SimplifiedCode {

    private String changedDate;
    private long changedByUserId;
    private String changedByUserName;
    private long codeId;
    private String code;
    private long weight;
    private boolean archived;
    private String validFrom;
    private String validTo;
    private String shortName;
    private String longName;

    public static SimplifiedCode fromMasterDataGenericEntity(final MasterDataGenericEntity entity) {
        return SimplifiedCode.builder()
                .changedDate(toStringValue(entity.getModifiedDate()))
                .changedByUserId(Long.parseLong(entity.getModifiedBy()))
                .changedByUserName("")
                .codeId(entity.getId())
                .code(entity.getCode())
                .weight(entity.getWeight())
                .archived(entity.isArchived())
                .validFrom(toStringValue(entity.getValidFrom()))
                .validTo(toStringValue(entity.getValidTo()))
                .shortName(entity.getShortName())
                .longName(entity.getLongName())
                .build();
    }

    private static String toStringValue(final LocalDateTime localDateTime) {
        return defaultIfNull(localDateTime.toString(), EMPTY);
    }
}
