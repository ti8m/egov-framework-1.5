package ch.ti8m.egov.testbase.repositories;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.testbase.entities.inheritance.mapped_superclass.TestMappedImplementingEntity;
import org.springframework.stereotype.Repository;

@ClassType(entityClass = TestMappedImplementingEntity.class)
@Repository
public class TestImplementingEntityRepository extends BaseRepositoryImpl<TestMappedImplementingEntity> {
}