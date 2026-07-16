package ch.ti8m.egov.framework.iam.persistence.model.role;

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
@Table(name = "EGOV_IAM_Role_Ln")
@Getter
@Setter
@NoArgsConstructor
public class RoleLn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String languageId;

    private String description;

    @Column(insertable = false, updatable = false)
    private Long roleid;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "roleid")
    private Role role;

}