package ch.ti8m.egov.framework.iam.persistence.model.ruleset;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EGOV_IAM_Condition")
@Getter
@Setter
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    private Boolean allowedForAllRoles = false;

    private Boolean allowedForAllStates = false;

    @OneToMany(mappedBy = "condition", cascade = CascadeType.ALL)
    @OrderBy("id DESC")
    @Setter(AccessLevel.NONE)
    private List<ConditionAllowedForState> conditionAllowedForStates = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "rulesetid")
    private RuleSet ruleSet;

    @OneToMany(mappedBy = "condition", cascade = CascadeType.ALL)
    @OrderBy("id DESC")
    @Setter(AccessLevel.NONE)
    private List<ConditionAllowedForRole> conditionAllowedForRoles = new ArrayList<>();

}