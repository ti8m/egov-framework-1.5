package ch.ti8m.egov.framework.iam.components;

import ch.ti8m.egov.framework.deployconfig.AdminConfigurationService;
import ch.ti8m.egov.framework.iam.persistence.repository.RoleMembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminCheckComponent {

    private static final String ADMIN_ROLE_NAME = "RO_Development_Admin";

    private final RoleMembershipRepository roleMembershipRepository;

    private final AdminConfigurationService adminConfigurationComponent;

    @Autowired
    public AdminCheckComponent(RoleMembershipRepository roleMembershipRepository, AdminConfigurationService adminConfigurationComponent) {
        this.roleMembershipRepository = roleMembershipRepository;
        this.adminConfigurationComponent = adminConfigurationComponent;
    }

    public boolean isAdmin(final String userId) {
        return adminConfigurationComponent.isActivated()
                && roleMembershipRepository.userIsMemberOfRole(userId, ADMIN_ROLE_NAME);
    }

}
