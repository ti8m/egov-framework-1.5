package ch.ti8m.egov.testbase.entities.relationship;

import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "test_sub_entity_many_to_one")
@FieldNameConstants
@Builder
@AllArgsConstructor
public class TestSubEntityManyToOne extends ArchivedModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;
}
