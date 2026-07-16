package ch.ti8m.egov.testbase.entities.inheritance.mapped_superclass;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
@AllArgsConstructor
abstract class TestMappedSuperClass extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String superField;

    public TestMappedSuperClass(String superField) {
        this.superField = superField;
    }

}