package ch.ti8m.egov.framework.iam.persistence.repository;

import ch.ti8m.egov.framework.iam.persistence.model.role.Role;
import ch.ti8m.egov.framework.iam.persistence.model.role.RoleMembership;

import java.time.LocalDateTime;
import java.util.function.UnaryOperator;

class RoleMembershipEntityFactory {

    private RoleMembershipEntityFactory() {
    }

    //////////////
    // Role
    /// ///////////
    public static Role role() {
        return role(UnaryOperator.identity());
    }

    public static Role role(final UnaryOperator<Role.RoleBuilder> customizer) {
        return customizer.apply(roleBuilder()).build();
    }

    private static Role.RoleBuilder roleBuilder() {
        return Role.builder()
                .roleName("THIS_ROLE_NAME")
                .id(17L);
    }

    //////////////
    // RoleMembership
    /// ///////////
    public static RoleMembership roleMembership() {
        return roleMembership(UnaryOperator.identity());
    }

    public static RoleMembership roleMembership(final UnaryOperator<RoleMembership.RoleMembershipBuilder> customizer) {
        return customizer.apply(roleMembershipBuilder()).build();
    }

    private static RoleMembership.RoleMembershipBuilder roleMembershipBuilder() {
        return RoleMembership.builder()
                .validFrom(LocalDateTime.now().minusDays(1))
                .validTo(LocalDateTime.now().plusDays(1));
    }

}
