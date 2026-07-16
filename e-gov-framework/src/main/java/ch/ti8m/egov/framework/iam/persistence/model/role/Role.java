package ch.ti8m.egov.framework.iam.persistence.model.role;


import ch.ti8m.egov.framework.iam.persistence.model.permission.PermissionDefinition;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ConditionAllowedForRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EGOV_IAM_Role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleName;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private List<RoleLn> lokalisierungen = new ArrayList<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @OrderBy("validFrom DESC")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private List<RoleMembership> roleMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private List<ConditionAllowedForRole> conditionAllowedForRoles = new ArrayList<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    @Setter(AccessLevel.NONE)
    @Builder.Default
    private List<PermissionDefinition> permissionDefinitions = new ArrayList<>();

}