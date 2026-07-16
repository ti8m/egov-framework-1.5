package ch.ti8m.egov.framework.deployconfig;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DatabaseConfigurationService {

    private static final String ERROR_SQL_DIALECT_NOT_SET_IN_APPLICATION_PROPERTIES = "Error: SQL Dialect not set in application properties";

    @Value("${egov.persistence.database.type}")
    private DatabaseType databaseType;

    @Getter
    @Value("${egov.persistence.database.naming.style:CAMEL_CASE}")
    private NamingStyle namingStyle;

    public boolean isMSSQLServer() {
        return databaseType == DatabaseType.SQLSERVER;
    }

    public boolean isPostgreSQL() {
        return databaseType == DatabaseType.POSTGRESQL;
    }

    public String getCurrentDateString() {
        if (isMSSQLServer()) {
            return "getdate()";
        }
        if (isPostgreSQL()) {
            return "CURRENT_TIMESTAMP";
        }
        return ERROR_SQL_DIALECT_NOT_SET_IN_APPLICATION_PROPERTIES;
    }

    public String getCastToBooleanString() {
        if (isMSSQLServer()) {
            return "?";
        }
        if (isPostgreSQL()) {
            return "cast(? as boolean)";
        }
        return ERROR_SQL_DIALECT_NOT_SET_IN_APPLICATION_PROPERTIES;
    }

    public String getTrueStatement() {
        if (isMSSQLServer()) {
            return "1";
        }
        if (isPostgreSQL()) {
            return "true";
        }
        return ERROR_SQL_DIALECT_NOT_SET_IN_APPLICATION_PROPERTIES;
    }

    public String getFalseStatement() {
        if (isMSSQLServer()) {
            return "0";
        }
        if (isPostgreSQL()) {
            return "false";
        }
        return ERROR_SQL_DIALECT_NOT_SET_IN_APPLICATION_PROPERTIES;
    }

}
