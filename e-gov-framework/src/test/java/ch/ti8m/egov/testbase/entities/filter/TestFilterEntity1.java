package ch.ti8m.egov.testbase.entities.filter;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = TestFilterEntity1.TABLE_E1)
public class TestFilterEntity1 extends ModifiableEntity {
    public static final String TABLE_E1 = "table1";
    protected static final String COLUMN_NAME = "Col1";
    @Id
    @Column(name = "TEId")
    private Integer id;

    @Column(name = "field0")
    private String field0;

    @Column(name = COLUMN_NAME)
    private String field1;

    @Column(name = "field2")
    private String field2;

    @OneToMany(mappedBy = "testEntity", cascade = CascadeType.PERSIST)
    private List<TestFilterEntity2> testEntity2s;

    @ManyToOne
    @JoinColumn(referencedColumnName = "TE4Id", name = "TestEntityManyId")
    private TestFilterEntityMany testEntityMany;

    @OneToOne
    @JoinColumn(referencedColumnName = "TE5Id", name = "TestEntityOneToId")
    private TestFilterEntityOneTo testEntityOneTo;

    @OneToOne(mappedBy = "testEntity")
    private TestFilterEntityOneFrom testEntityOneFrom;

    @OneToOne(mappedBy = "testEntity")
    private TestFilterEntityOneFromClean testEntityOneFromClean;

    @OneToOne(mappedBy = "testEntity")
    private TestEntityArchivedModifiable testEntityArchivedModifiable;

}
