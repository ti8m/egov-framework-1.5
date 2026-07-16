package ch.ti8m.egov.testbase.repositories;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.testbase.entities.inheritance.table_per_class.TestInheritanceTablePerClassImplementingEntity;
import org.springframework.stereotype.Repository;

@ClassType(entityClass = TestInheritanceTablePerClassImplementingEntity.class)
@Repository
public class TestTablePerClassImplementingEntityRepository extends BaseRepositoryImpl<TestInheritanceTablePerClassImplementingEntity> {

}