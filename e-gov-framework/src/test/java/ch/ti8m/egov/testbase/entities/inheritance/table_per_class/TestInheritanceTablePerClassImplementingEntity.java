package ch.ti8m.egov.testbase.entities.inheritance.table_per_class;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "test_table_per_class_implementing")
@AllArgsConstructor
@Builder
public class TestInheritanceTablePerClassImplementingEntity extends TestInheritanceTablePerClassSuperEntity {
    private String implementingField;

    public TestInheritanceTablePerClassImplementingEntity(String implementingField, String superField) {
        super(superField);
        this.implementingField = implementingField;
    }

}