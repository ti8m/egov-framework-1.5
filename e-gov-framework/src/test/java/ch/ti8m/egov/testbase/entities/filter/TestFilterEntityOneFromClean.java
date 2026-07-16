package ch.ti8m.egov.testbase.entities.filter;

import ch.ti8m.egov.framework.persistence.base.ModifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import static ch.ti8m.egov.testbase.entities.filter.TestFilterEntityOneFromClean.TABLE_E8;

@Entity
@Table(name = TABLE_E8)
class TestFilterEntityOneFromClean extends ModifiableEntity {
    protected static final String TABLE_E8 = "table8";

    @Id
    @Column(name = "TE8Id")
    private Integer id;

    @Column(name = "field8")
    private String field8;

    @OneToOne
    @JoinColumn(name = "TestEntityId")
    private TestFilterEntity1 testEntity;

}
