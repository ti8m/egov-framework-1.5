package ch.ti8m.egov.testbase.entities.inheritance.table_per_class;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class TestInheritanceTablePerClassSuperEntity extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String superField;
    @OneToMany(mappedBy = "testTablePerClassSuperEntity",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<TestSuperSubEntity> testMappedToSuperClassEntities = new ArrayList<>();

    public TestInheritanceTablePerClassSuperEntity(String superField) {
        this.superField = superField;
    }
}