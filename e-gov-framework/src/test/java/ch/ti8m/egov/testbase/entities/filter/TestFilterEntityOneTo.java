package ch.ti8m.egov.testbase.entities.filter;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = TestFilterEntityOneTo.TABLE_E5)
class TestFilterEntityOneTo extends ModifiableEntity {
    public static final String TABLE_E5 = "table5";

    @Id
    @Column(name = "TE5Id")
    private Integer id;

    @Column(name = "field5")
    private String field5;

    @OneToOne(mappedBy = "testEntityOneTo")
    private TestFilterEntity1 testEntity;

}