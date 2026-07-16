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
@Table(name = TestFilterEntity2.TABLE_E2)
public class TestFilterEntity2 extends ModifiableEntity {
    public static final String TABLE_E2 = "table2";

    @Id
    @Column(name = "TE2Id")
    private Integer id;

    @Column(name = "Field2")
    private String field2;

    @Column(name = "Field2_1")
    private String field2_1;

    @ManyToOne
    @JoinColumn(referencedColumnName = "TEId", name = "TestEntityId")
    private TestFilterEntity1 testEntity;

    @OneToMany(mappedBy = "testEntity2", cascade = CascadeType.PERSIST)
    private List<TestFilterEntity3> testEntity3s;

    @OneToMany(mappedBy = "testEntity2", cascade = CascadeType.PERSIST)
    private List<TestFilterEntityMany> TestEntityManys;

}