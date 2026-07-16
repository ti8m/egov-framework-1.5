package ch.ti8m.egov.testbase.entities;

import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CP_TEST")
@Getter
@Setter
public class TestArchivedModifiableEntity extends ArchivedModifiableEntity {

    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "testField")
    String testField;

    @Column(name = "testField1")
    String testField1;

    @Column(name = "testField2")
    String testField2;

    @Column(name = "testNumber")
    Integer number;

    @Column(name = "isTestEntity")
    Boolean isTestEntity;
}
