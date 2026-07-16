package ch.ti8m.egov.testbase.repositories;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import ch.ti8m.egov.testbase.entities.relationship.TestSubEntityOneToMany;
import org.springframework.stereotype.Repository;

@ClassType(entityClass = TestSubEntityOneToMany.class)
@Repository
public class TestSubEntityOneToManyRepository extends BaseRepositoryImpl<TestSubEntityOneToMany> {
}
