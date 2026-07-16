package ch.ti8m.egov.testbase.entities.filter;

import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import static ch.ti8m.egov.testbase.entities.filter.TestEntityArchivedModifiable.TABLE_E7;

@Entity
@Table(name = TABLE_E7)
public class TestEntityArchivedModifiable extends ArchivedModifiableEntity {

    protected static final String TABLE_E7 = "table7";

    @Id
    @Column(name = "TE7Id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "TestEntityId")
    private TestFilterEntity1 testEntity;

    @Column(name = "field7")
    private String field7;
}
