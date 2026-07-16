package ch.ti8m.egov.framework.iam.components;


import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.iam.persistence.model.permission.PermissionDefinition;
import ch.ti8m.egov.framework.iam.persistence.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionManagementComponent {

    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionManagementComponent(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<PermissionDefinition> getUserPermissions(
            final PermissionOperation operation,
            final String entityTitle
    ) {
        if (operation == null || entityTitle == null) {
            return getAllUserPermissions();
        }
        return permissionRepository.getUserPermissions(
                DataHolder.getUserId(),
                operation,
                entityTitle
        );
    }

    public List<PermissionDefinition> getAllUserPermissions() {
        return permissionRepository.getAllUserPermissions(
                DataHolder.getUserId()
        );
    }

}
