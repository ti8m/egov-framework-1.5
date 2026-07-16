package ch.ti8m.egov.testbase.tools;

import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import ch.ti8m.egov.testbase.entities.relationship.TestSubEntityOneToMany;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class EntityFactory {
    public static TestEntity newMoritzEntity() {
        final TestEntity testEntity = new TestEntity(
                "moritz",
                "baumotte",
                "someValue",
                true,
                LocalDateTime.of(2020, 1, 1, 12, 0),
                ZonedDateTime.of(LocalDateTime.of(2020, 1, 1, 12, 0), ZoneId.of("Europe/Berlin"))
        );
        testEntity.setTestSubEntitiesOneToMany(List.of(
                new TestSubEntityOneToMany("content_1", testEntity),
                new TestSubEntityOneToMany("content_2", testEntity),
                new TestSubEntityOneToMany("content_3", testEntity)
        ));
        return testEntity;
    }

    public static TestEntity newNullFieldEntity() {
        return new TestEntity(
                null,
                null,
                null,
                false,
                null,
                null
        );
    }
}
