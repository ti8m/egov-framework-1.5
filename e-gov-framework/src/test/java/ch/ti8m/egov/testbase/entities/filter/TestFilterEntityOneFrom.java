package ch.ti8m.egov.testbase.entities.filter;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import static ch.ti8m.egov.testbase.entities.filter.TestFilterEntityOneFrom.TABLE_E6;

@Entity
@Table(name = TABLE_E6)
class TestFilterEntityOneFrom extends ModifiableEntity {
    public static final String TABLE_E6 = "table6";

    @Id
    @Column(name = "TE6Id")
    private Integer id;

    @Column(name = "field6")
    private String field6;

    @OneToOne
    @JoinColumn(referencedColumnName = "TEId", name = "TestEntityId")
    private TestFilterEntity1 testEntity;

}