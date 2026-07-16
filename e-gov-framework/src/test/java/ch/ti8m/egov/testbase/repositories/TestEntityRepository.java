package ch.ti8m.egov.testbase.repositories;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.testbase.entities.relationship.TestEntity;
import org.springframework.stereotype.Repository;

@ClassType(entityClass = TestEntity.class)
@Repository
public class TestEntityRepository extends BaseRepositoryImpl<TestEntity> {

}