package ch.ti8m.egov.mdm.api.types;

import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultCode {

    private long codeId;
    private String code;
    private long weight;
    private String shortName;
    private String longName;
    private Map<String, String> fields;
    private boolean archived;

    public static ResultCode fromEntity(final MasterDataGenericEntity MasterDataGenericEntity) {
        final Map<String, String> additionalFields = MasterDataGenericEntity.getAdditionalContent().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));

        return ResultCode.builder()
                .archived(MasterDataGenericEntity.isArchived())
                .code(MasterDataGenericEntity.getCode())
                .codeId(MasterDataGenericEntity.getId())
                .fields(additionalFields)
                .longName(MasterDataGenericEntity.getLongName())
                .shortName(MasterDataGenericEntity.getShortName())
                .weight(MasterDataGenericEntity.getWeight())
                .build();
    }
}
