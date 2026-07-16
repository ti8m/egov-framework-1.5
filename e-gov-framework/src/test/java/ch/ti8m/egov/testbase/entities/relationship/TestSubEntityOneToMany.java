package ch.ti8m.egov.testbase.entities.relationship;

import ch.ti8m.egov.framework.persistence.base.ArchivedModifiableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "test_sub_entity_one_to_many")
@FieldNameConstants
@Builder
@AllArgsConstructor
public class TestSubEntityOneToMany extends ArchivedModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_entity_id")
    private TestEntity testEntity;

    public TestSubEntityOneToMany(String content, TestEntity testEntity) {
        this.content = content;
        this.testEntity = testEntity;
    }
}
