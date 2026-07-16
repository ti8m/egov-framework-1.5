package ch.ti8m.egov.testbase.entities.relationship;

import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "test_sub_entity_one_to_one_maps_id")
@FieldNameConstants
@Builder
@AllArgsConstructor
public class TestSubEntityOneToOneMapsId extends ArchivedModifiableEntity {

    @Id
    private Long id;

    @Column
    private String content;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @MapsId
    private TestEntity testEntity;
}
