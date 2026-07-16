package ch.ti8m.egov.framework.iam.persistence.model.permission;

import ch.ti8m.egov.framework.iam.api.model.PermissionOperation;
import ch.ti8m.egov.framework.iam.persistence.model.role.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EGOV_IAM_Permission_Definition")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String entityTitle;

    private String sqlQuery;

    @Enumerated(EnumType.STRING)
    private PermissionOperation operation;

    @Column(insertable = false, updatable = false)
    private Long roleid;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(referencedColumnName = "id", name = "roleid")
    private Role role;

}