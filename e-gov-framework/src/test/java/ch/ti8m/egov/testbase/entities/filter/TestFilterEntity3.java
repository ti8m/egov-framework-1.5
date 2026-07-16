package ch.ti8m.egov.testbase.entities.filter;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = TestFilterEntity3.TABLE_E3)
class TestFilterEntity3 extends ModifiableEntity {
    public static final String TABLE_E3 = "table3";

    @Id
    @Column(name = "TE3Id")
    private Integer id;

    @Column(name = "Field3")
    private String field3;

    @ManyToOne
    @JoinColumn(referencedColumnName = "TE2Id", name = "TestEntity2Id")
    private TestFilterEntity2 testEntity2;

}