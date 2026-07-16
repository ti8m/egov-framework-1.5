package ch.ti8m.egov.testbase.entities.inheritance.mapped_superclass;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "test_implementing_entity")
@AllArgsConstructor
public class TestMappedImplementingEntity extends TestMappedSuperClass {
    private String implementingField;

    public TestMappedImplementingEntity(String implementingField, String superField) {
        super(superField);
        this.implementingField = implementingField;
    }

}