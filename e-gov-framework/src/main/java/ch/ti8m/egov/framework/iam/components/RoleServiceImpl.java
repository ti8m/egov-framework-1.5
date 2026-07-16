package ch.ti8m.egov.framework.iam.components;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.iam.api.java.RoleService;
import ch.ti8m.egov.framework.iam.persistence.model.role.Role;
import ch.ti8m.egov.framework.iam.persistence.model.role.RoleMembership;
import ch.ti8m.egov.framework.iam.persistence.repository.RoleMembershipRepository;
import ch.ti8m.egov.framework.iam.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final RoleMembershipRepository roleMembershipRepository;

    @Autowired
    public RoleServiceImpl(final RoleRepository roleRepository, final RoleMembershipRepository roleMembershipRepository) {
        this.roleRepository = roleRepository;
        this.roleMembershipRepository = roleMembershipRepository;
    }

    @Override
    public void addToRole(
            final String userId,
            final Role role,
            final LocalDateTime validFrom,
            final LocalDateTime validTo
    ) {
        final RoleMembership roleMembership = new RoleMembership();
        roleMembership.setRole(role);
        roleMembership.setUserId(userId);
        roleMembership.setValidFrom(validFrom);
        roleMembership.setValidTo(validTo);
        roleMembershipRepository.save(roleMembership);
    }

    @Override
    public void addToRole(final String userId, final String roleName, final LocalDateTime validFrom, final LocalDateTime validTo) {
        final Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EGovException("unable to find rolename: " + roleName));
        addToRole(
                userId,
                role,
                validFrom,
                validTo
        );
    }

    @Override
    public void saveRole(final Role role) {
        roleRepository.save(role);
    }

    @Override
    public List<String> getRolesByUserId(final String userId) {
        return roleMembershipRepository.getRolesByUserId(userId)
                .stream()
                .map(RoleMembership::getRole)
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getMembershipsOfRole(final String roleName) {
        return roleMembershipRepository.getMembershipsOfRole(roleName)
                .stream()
                .map(RoleMembership::getUserId)
                .collect(Collectors.toList());
    }

}
