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

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EGOV_MDM_VOCABULARY_LN")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VocabularyLn extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    private String languageCode;

    @Column(length = 200)
    private String name;

    @Column(length = 200)
    private String description;

    @ManyToOne
    @JoinColumn(name = "vocabularycode", referencedColumnName = "code")
    private Vocabulary vocabulary;
}
