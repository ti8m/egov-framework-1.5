package ch.ti8m.egov.mdm.persistence.entity;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "EGOV_MDM_FIELD_DEFINITION")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FieldDefinition extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(length = 20)
    private String type;

    @Column
    private String defaultValue;

    @ManyToOne
    @JoinColumn(name = "vocabularycode", referencedColumnName = "code")
    private Vocabulary vocabulary;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @Builder.Default
    private List<FieldDefinitionLn> localizations = new ArrayList<>();

    @SneakyThrows
    @JsonIgnore
    @Transient
    public <T> T getDefaultValue(final ObjectMapper objectMapper) {
        return objectMapper.convertValue(defaultValue, (Class<T>) Class.forName(type));
    }

    @SneakyThrows
    @Transient
    @JsonIgnore
    public void setDefaultValue(final String defaultValue, final ObjectMapper objectMapper) {
        this.defaultValue = objectMapper.writeValueAsString(defaultValue);
    }

    @Transient
    @JsonIgnore
    public Optional<FieldDefinitionLn> getLocalization(final String languageCode) {
        return localizations.stream()
                .filter(fieldDefinitionLn -> Objects.equals(fieldDefinitionLn.getLanguageCode(), languageCode))
                .findFirst();
    }

}
