package ch.ti8m.egov.framework.iam.api.rest;

import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.iam.components.PermissionManagementComponent;
import ch.ti8m.egov.framework.iam.persistence.model.permission.PermissionDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/iam/v1")
public class IAMController {

    private final PermissionManagementComponent permissionManagementComponent;

    @Autowired
    public IAMController(PermissionManagementComponent permissionManagementComponent) {
        this.permissionManagementComponent = permissionManagementComponent;
    }

    @GetMapping("/permissions")
    public List<PermissionDefinition> getSubTypTitel(
            @RequestParam("operation") final PermissionOperation operation,
            @RequestParam("entityTitle") final String entityTitle
    ) {
        return permissionManagementComponent.getUserPermissions(
                operation,
                entityTitle
        );
    }

}
