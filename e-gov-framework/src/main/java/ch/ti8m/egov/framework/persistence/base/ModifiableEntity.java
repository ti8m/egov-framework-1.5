package ch.ti8m.egov.framework.persistence.base;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@ToString(of = {"modifiedDate"})
@Getter
@Setter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public abstract class ModifiableEntity implements Serializable {

    /**
     * Field length for <cite>Code</cite> enumeration constants.
     */
    public static final int CODE_LENGTH = 20;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String createdBy;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String modifiedBy;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime createdDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private LocalDateTime modifiedDate;

    @PrePersist
    public void prePersist() {
        if (createdBy == null) {
            createdBy = DataHolder.getUserId();
        }
        modifiedBy = DataHolder.getUserId();
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        modifiedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        modifiedBy = DataHolder.getUserId();
        modifiedDate = LocalDateTime.now();
    }

    // override where status check is necessary
    @JsonIgnore
    @Transient
    public Enum<?> getAggregateStatus() {
        return null;
    }

}
