package ch.ti8m.egov.framework.persistence.query;

import lombok.Data;

@Data
public class JoinConfiguration {

    final boolean fieldNameToBeDatabaseColumnNameAllowed;
    final boolean oneToManyJoinAllowed;
    final boolean archivedEntitiesIncluded;
    final String dbAwareFalseStatement;

}
