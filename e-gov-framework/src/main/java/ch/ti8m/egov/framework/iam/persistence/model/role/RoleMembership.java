package ch.ti8m.egov.framework.iam.persistence.model.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "EGOV_IAM_Role_Membership")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid")
    private String userId;

    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    @Column(insertable = false, updatable = false)
    private Long roleid;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "roleid")
    private Role role;

}