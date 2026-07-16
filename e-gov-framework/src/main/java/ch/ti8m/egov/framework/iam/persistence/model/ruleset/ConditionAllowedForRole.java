package ch.ti8m.egov.framework.iam.persistence.model.ruleset;

import ch.ti8m.egov.framework.iam.persistence.model.role.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EGOV_IAM_Condition_Allowed_For_Role")
@Getter
@Setter
@NoArgsConstructor
public class ConditionAllowedForRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(insertable = false, updatable = false)
    private Long roleid;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(referencedColumnName = "id", name = "roleid")
    private Role role;

    @Column(insertable = false, updatable = false)
    private Long conditionid;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "conditionid")
    private Condition condition;

}