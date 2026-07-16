package ch.ti8m.egov.mdm.persistence.entity;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EGOV_MDM_RECORD")
@Getter
@Setter
@Builder
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
public class MasterDataGenericEntity extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, length = 200)
    private String code;

    @Column(nullable = false, length = 200)
    private String vocabularyCode;

    @Column(length = 20)
    private String languageCode;

    private int weight;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    private String shortName;

    private String longName;

    @Convert(converter = ObjectConverter.class)
    @Builder.Default
    @Column(length = 32000)
    private Map<String, Object> additionalContent = new HashMap<>();

    @Column(nullable = false)
    private boolean archived;

    public MasterDataGenericEntity(final String vocabularyCode,
                                   final String code,
                                   final String language,
                                   final String shortName,
                                   final String longName,
                                   final LocalDateTime validFrom,
                                   final LocalDateTime validTo,
                                   final Map<String, Object> additionalContent) {

        this.vocabularyCode = vocabularyCode;
        this.code = code;
        this.archived = false;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.additionalContent = additionalContent;
        this.languageCode = language;
        this.shortName = shortName;
        this.longName = longName;
    }

}
