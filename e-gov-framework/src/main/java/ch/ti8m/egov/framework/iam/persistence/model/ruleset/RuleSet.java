package ch.ti8m.egov.framework.iam.persistence.model.ruleset;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "EGOV_IAM_Rule_Set")
@Getter
@Setter
@NoArgsConstructor
public class RuleSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @OneToOne(mappedBy = "ruleSet", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private Condition condition;

    @Column(length = 8000)
    @Convert(converter = ValidationEntityConverter.class)
    private ValidationEntity validationEntity;

    private String ruleSetCode;

    private String category;

    private Integer ruleSetPriority;

}