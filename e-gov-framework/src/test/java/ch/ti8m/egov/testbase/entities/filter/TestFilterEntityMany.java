package ch.ti8m.egov.testbase.entities.filter;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = TestFilterEntityMany.TABLE_E4)
class TestFilterEntityMany extends ModifiableEntity {
    public static final String TABLE_E4 = "table4";

    @Id
    @Column(name = "TE4Id")
    private Integer id;

    @Column(name = "field4")
    private String field4;

    @OneToMany(mappedBy = "testEntityMany", cascade = CascadeType.PERSIST)
    private List<TestFilterEntity1> testEntityList;

    @ManyToOne
    @JoinColumn(referencedColumnName = "TE2Id", name = "TestEntity2Id")
    private TestFilterEntity2 testEntity2;

}