package ch.ti8m.egov.framework.validation.command;

import ch.ti8m.egov.framework.persistence.base.BaseRepositoryImpl;
import ch.ti8m.egov.framework.persistence.base.ClassType;
import org.springframework.stereotype.Repository;

@ClassType(entityClass = Command.class)
@Repository
public class CommandRepository extends BaseRepositoryImpl<Command> {

}
