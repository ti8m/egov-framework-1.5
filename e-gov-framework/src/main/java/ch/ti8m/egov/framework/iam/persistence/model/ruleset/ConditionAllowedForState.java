package ch.ti8m.egov.framework.iam.persistence.model.ruleset;

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
@Table(name = "EGOV_IAM_Condition_Allowed_For_State")
@Getter
@Setter
@NoArgsConstructor
public class ConditionAllowedForState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String state;

    @Column(insertable = false, updatable = false)
    private Long conditionid;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "conditionid")
    private Condition condition;

}