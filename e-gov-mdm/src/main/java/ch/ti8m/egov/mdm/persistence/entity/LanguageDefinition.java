package ch.ti8m.egov.mdm.persistence.entity;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EGOV_MDM_LANGUAGE_DEFINITION")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class LanguageDefinition extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(length = 20)
    private String languageCode;

    @Column(insertable = false, updatable = false, length = 200)
    private String vocabularycode;

    @ManyToOne
    @JoinColumn(name = "vocabularycode", referencedColumnName = "code")
    private Vocabulary vocabulary;

}
