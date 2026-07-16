package ch.ti8m.egov.framework.persistence.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public abstract class ArchivedModifiableEntity extends ModifiableEntity {

    /*
    Add the filter annotation to each OneToMany relation that associates a Collection<? extends ArchivedModifiableEntity>
    @Filter(name = ArchivedModifiableEntity.ARCHIVED_FILTER)
     */

    public static final String ARCHIVED_FIELD = "Archived";
    public static final String ARCHIVED_FILTER = "archivedFilter";
    public static final String ARCHIVED_STATUS = "archivedStatus";
    public static final String ARCHIVED_CONDITION = ArchivedModifiableEntity.ARCHIVED_FIELD + " = :" + ArchivedModifiableEntity.ARCHIVED_STATUS;

    @Column(nullable = false)
    private boolean archived = false;

}
