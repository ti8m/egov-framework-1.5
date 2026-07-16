package ch.ti8m.egov.testbase.entities.inheritance.table_per_class;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "test_super_sub_entity")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TestSuperSubEntity extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mappedEntityContentField;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "testTablePerClassSuperClass")
    private TestInheritanceTablePerClassSuperEntity testTablePerClassSuperEntity;
}