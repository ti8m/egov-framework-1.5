package ch.ti8m.egov.mdm.persistence.entity;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import ch.ti8m.egov.mdm.persistence.entity.enumerations.NameValidationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.collections4.IterableUtils;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EGOV_MDM_VOCABULARY")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
public class Vocabulary extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false, length = 200)
    private String code;

    @Column(nullable = false)
    private boolean modifiable;

    @Column(nullable = false)
    private boolean sortable;

    @Column(nullable = false, length = 20)
    @ColumnDefault("'LONG_AND_SHORT_NAME'")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NameValidationType nameValidationType = NameValidationType.LONG_AND_SHORT_NAME;

    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FieldDefinition> fields = new ArrayList<>();

    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL)
    @Builder.Default
    private List<LanguageDefinition> languages = new ArrayList<>();

    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL)
    @Builder.Default
    private List<VocabularyLn> localizations = new ArrayList<>();

    @JsonIgnore
    @Transient
    public Optional<VocabularyLn> getLocalization(final String languageCode) {
        return Optional.ofNullable(IterableUtils.find(localizations, vocabularyLn -> Objects.equals(languageCode, vocabularyLn.getLanguageCode())));
    }

}
