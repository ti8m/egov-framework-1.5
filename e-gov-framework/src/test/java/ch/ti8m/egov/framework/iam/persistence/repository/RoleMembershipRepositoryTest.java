package ch.ti8m.egov.framework.iam.persistence.repository;

import ch.ti8m.egov.framework.iam.persistence.model.role.Role;
import ch.ti8m.egov.framework.iam.persistence.model.role.RoleMembership;
import ch.ti8m.egov.testbase.TestApplicationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static ch.ti8m.egov.framework.iam.persistence.repository.RoleMembershipEntityFactory.role;
import static ch.ti8m.egov.framework.iam.persistence.repository.RoleMembershipEntityFactory.roleMembership;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ToDo: This Test needs rework as it fails if other tests are active.
 * We isolated the problem by deactivating all other tests. And fixed this isolated one by
 * a separate test-special profile.
 * If you activate the others again, the test will fail again.
 * The local maven run works fine.
 * We assume that the API version of the testcontainer library is not compatible with the docker version used in the CI pipeline.
 * This as locally we updated both docker and library version.
 */
@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class RoleMembershipRepositoryTest extends TestApplicationContext {

    private static final String USER_ID = "12";

    @Autowired
    RoleMembershipRepository roleMembershipRepository;

    @Autowired
    RoleRepository roleRepository;

    @PersistenceContext
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        try {
            entityManager.createNativeQuery("DELETE FROM EGOV_IAM_Role").executeUpdate();
        } catch (final Exception ignore) {
            log.info("unexpected exception if EGOV_IAM_Role does not exit (race condition?)");
        }
    }

    @Test
    @Disabled
    void getMembershipsOfRole_returnsMemberships() {
        final Role role = role(roleBuilder -> roleBuilder.id(null));
        final RoleMembership roleMembership = roleMembership();
        roleMembership.setUserId(USER_ID);
        role.getRoleMemberships().add(roleMembership);
        roleMembership.setRole(role);
        roleMembership.setRoleid(role.getId());

        roleRepository.save(roleMembership.getRole());

        final List<RoleMembership> memberships = roleMembershipRepository.getMembershipsOfRole(role().getRoleName());

        assertThat(memberships).isNotEmpty();
    }

    @Test
    @Disabled
    void getRolesByUserId_returnsMemberships() {
        final Role role = role(roleBuilder -> roleBuilder.id(null));
        final RoleMembership roleMembership = roleMembership();
        roleMembership.setUserId(USER_ID);
        role.getRoleMemberships().add(roleMembership);
        roleMembership.setRole(role);
        roleMembership.setRoleid(role.getId());

        roleRepository.save(role);

        final List<RoleMembership> memberships = roleMembershipRepository.getRolesByUserId(USER_ID);

        assertThat(memberships).isNotEmpty();
    }

    @Test
    @Disabled
    void terminateForUserId_terminatesCorrectly() {
        final Role role = role(roleBuilder -> roleBuilder.id(null));
        final RoleMembership roleMembership = roleMembership();
        roleMembership.setUserId(USER_ID);
        role.getRoleMemberships().add(roleMembership);
        roleMembership.setRole(role);
        roleMembership.setRoleid(role.getId());

        roleRepository.save(role);

        final List<RoleMembership> memberships = roleMembershipRepository.getRolesByUserId(USER_ID);
        assertThat(memberships).isNotEmpty();

        roleMembershipRepository.terminateForUserId(USER_ID, role.getRoleName());

        final List<RoleMembership> updatedMemberships = roleMembershipRepository.getRolesByUserId(USER_ID);
        assertThat(updatedMemberships).isEmpty();
    }

}