package ch.ti8m.egov.testbase.entities.relationship;

import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "test_entity")
@FieldNameConstants
@Builder
@AllArgsConstructor
public class TestEntity extends ArchivedModifiableEntity {

    public static final String SOME_VALUE_COLUMN_NAME = "some_value_col";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    @Column(name = SOME_VALUE_COLUMN_NAME)
    private String someValue;
    private boolean active;
    private LocalDateTime localDateTime;
    private ZonedDateTime zonedDateTime;

    @OneToMany(mappedBy = "testEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestSubEntityOneToMany> testSubEntitiesOneToMany = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private TestSubEntityManyToOne testSubEntityManyToOne;

    public TestEntity(String firstname, String lastname, String someValue, boolean active, LocalDateTime localDateTime, ZonedDateTime zonedDateTime) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.someValue = someValue;
        this.active = active;
        this.localDateTime = localDateTime;
        this.zonedDateTime = zonedDateTime;
    }

    public void addTestSubEntityOneToMany(TestSubEntityOneToMany testSubEntityOneToMany) {
        if (testSubEntitiesOneToMany == null) {
            testSubEntitiesOneToMany = new ArrayList<>();
        }
        testSubEntitiesOneToMany.add(testSubEntityOneToMany);
        testSubEntityOneToMany.setTestEntity(this);
    }
}
