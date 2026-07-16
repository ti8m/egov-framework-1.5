package ch.ti8m.egov.framework.iam.api.java;

import ch.ti8m.egov.framework.iam.persistence.model.role.Role;

import java.time.LocalDateTime;
import java.util.List;

public interface RoleService {

    void addToRole(
            final String userId,
            final Role role,
            final LocalDateTime validFrom,
            final LocalDateTime validTo
    );

    void addToRole(
            final String userId,
            final String roleName,
            final LocalDateTime validFrom,
            final LocalDateTime validTo
    );

    void saveRole(
            final Role role
    );

    List<String> getRolesByUserId(
            final String userId
    );

    List<String> getMembershipsOfRole(String roleName);
}
