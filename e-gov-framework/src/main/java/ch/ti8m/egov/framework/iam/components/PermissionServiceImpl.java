package ch.ti8m.egov.framework.iam.components;

import ch.ti8m.egov.framework.deployconfig.DevelopmentConfigurationService;
import ch.ti8m.egov.framework.exceptionhandling.model.NoPermissionForEntityException;
import ch.ti8m.egov.framework.iam.api.java.PermissionService;
import ch.ti8m.egov.framework.iam.api.model.AllowedRecordsQuery;
import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.iam.persistence.model.permission.PermissionDefinition;
import ch.ti8m.egov.framework.iam.persistence.repository.PermissionRepository;
import jakarta.persistence.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Component
public class PermissionServiceImpl implements PermissionService {

    private static final String INNER_RESULT_NAME = "innerResult";

    private final PermissionRepository permissionRepository;

    private final ClassUtilityComponent classUtilityComponent;

    private final AdminCheckComponent adminCheckComponent;

    private final DevelopmentConfigurationService developmentConfigurationComponent;

    @Autowired
    public PermissionServiceImpl(final PermissionRepository permissionRepository, final ClassUtilityComponent classUtilityComponent, final AdminCheckComponent adminCheckComponent, final DevelopmentConfigurationService developmentConfigurationComponent) {
        this.permissionRepository = permissionRepository;
        this.classUtilityComponent = classUtilityComponent;
        this.adminCheckComponent = adminCheckComponent;
        this.developmentConfigurationComponent = developmentConfigurationComponent;
    }

    private static String getPermissionsString(final List<PermissionDefinition> permissionDefinitions) {
        final StringJoiner logJoiner = new StringJoiner(System.lineSeparator() + System.lineSeparator());
        permissionDefinitions.forEach(permissionDefinition -> {
            final String logBuilder = "\tQuery:\t"
                    + permissionDefinition.getSqlQuery()
                    + System.lineSeparator()
                    + "\tRole:\t"
                    + permissionDefinition.getRole().getRoleName();
            logJoiner.add(logBuilder);
        });
        return logJoiner.toString();
    }

    @Override
    public AllowedRecordsQuery getAllowedRecordsQuery(
            final String userId,
            final PermissionOperation operation,
            final Class<?> entityClass
    ) {
        if (adminCheckComponent.isAdmin(userId)) {
            if (log.isInfoEnabled()) {
                log.info(String.format(
                                "Admin (%s) access to entity %s and operation %s",
                                userId,
                                entityClass.getName(),
                                operation.name()
                        )
                );
            }
            return getAdminQuery(entityClass);
        }
        final List<PermissionDefinition> permissionDefinitions = permissionRepository.getUserPermissions(
                userId,
                operation,
                entityClass.getAnnotation(Table.class).name()
        );
        log(permissionDefinitions, userId, operation, entityClass.getAnnotation(Table.class).name());
        if (permissionDefinitions.isEmpty()) {
            throw new NoPermissionForEntityException(
                    entityClass.getAnnotation(Table.class).name(),
                    operation.name(),
                    userId
            );
        }
        final String idFieldName = ClassUtilityComponent.getIdFieldName(entityClass);
        return buildAllowedRecordsQuery(permissionDefinitions, idFieldName, userId);
    }

    @Override
    public AllowedRecordsQuery getAllowedRecordsQuery(
            final String userId,
            final PermissionOperation operation,
            final String tableName,
            final String idFieldName
    ) {
        if (adminCheckComponent.isAdmin(userId)) {
            if (log.isInfoEnabled()) {
                log.info(String.format(
                                "Admin (%s) access to table %s and operation %s",
                                userId,
                                tableName,
                                operation.name()
                        )
                );
            }
            return getAdminQuery(tableName, idFieldName);
        }
        final List<PermissionDefinition> permissionDefinitions = permissionRepository.getUserPermissions(
                userId,
                operation,
                tableName
        );
        if (permissionDefinitions.isEmpty()) {
            throw new NoPermissionForEntityException(
                    tableName,
                    operation.name(),
                    userId
            );
        }
        log(permissionDefinitions, userId, operation, tableName);
        return buildAllowedRecordsQuery(permissionDefinitions, idFieldName, userId);
    }

    private void log(
            final List<PermissionDefinition> permissionDefinitions,
            final String userId,
            final PermissionOperation operation,
            final String tableName
    ) {
        if (developmentConfigurationComponent.isPermissionLogActivated()) {
            final String permissionsString = getPermissionsString(permissionDefinitions);
            // Liferay's slf4j implementation removes new-lines. Need to print whole statement in one go because of parallelization.
            System.out.println(
                    String.format(
                            "Permissions for user %s, table %s, operation %s:",
                            userId,
                            tableName,
                            operation.name()
                    )
                            + System.lineSeparator()
                            + "["
                            + System.lineSeparator()
                            + permissionsString
                            + System.lineSeparator()
                            + "]"
            );
        }
    }

    private AllowedRecordsQuery buildAllowedRecordsQuery(
            final List<PermissionDefinition> permissionDefinitions,
            final String idFieldName,
            final String userId
    ) {
        final List<Object> parameters = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("SELECT DISTINCT ")
                .append(INNER_RESULT_NAME).append(".").append(idFieldName)
                .append(" FROM (");

        if (permissionDefinitions.isEmpty()) {
            stringBuilder.append(getNullResultQuery(idFieldName));
        } else {
            final StringJoiner stringJoiner = new StringJoiner(" UNION ");
            permissionDefinitions.forEach(permissionDefinition -> {
                stringJoiner.add("(" + permissionDefinition.getSqlQuery() + ")");
                final long userIdCount = permissionDefinition.getSqlQuery().chars().filter(ch -> ch == '?').count();
                for (long i = 0; i < userIdCount; i++) {
                    parameters.add(userId);
                }
            });
            stringBuilder.append(stringJoiner);
        }

        stringBuilder
                .append(") ")
                .append(INNER_RESULT_NAME);
        return new AllowedRecordsQuery(
                stringBuilder.toString(),
                parameters
        );
    }

    private String getNullResultQuery(
            final String idFieldName
    ) {
        return "SELECT void.* FROM (SELECT NULL " + idFieldName + ") void WHERE 1=0";
    }

    private AllowedRecordsQuery getAdminQuery(final Class<?> entityClass) {
        final String tableName = entityClass.getAnnotation(Table.class).name();
        final String idFieldName = ClassUtilityComponent.getIdFieldName(entityClass);

        return getAdminQuery(tableName, idFieldName);
    }

    private AllowedRecordsQuery getAdminQuery(final String tableName, final String idFieldName) {
        return new AllowedRecordsQuery(
                "SELECT " + idFieldName + " FROM " + tableName,
                Collections.emptyList()
        );
    }

}

