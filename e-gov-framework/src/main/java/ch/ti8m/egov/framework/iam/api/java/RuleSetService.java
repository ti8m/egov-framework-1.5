package ch.ti8m.egov.framework.iam.api.java;

import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;

import java.util.List;
import java.util.Optional;

public interface RuleSetService {

    RuleSet getRuleSet(
            final String userId,
            final String action,
            final Enum<?> aggregateState
    );

    RuleSet findByRuleSetCode(
            final String ruleSetCode
    );

    Optional<RuleSet> getRuleSetById(int ruleSetId);

    List<RuleSet> getRuleSetsByIds(List<Integer> ids);

    List<String> getApplicableActions(
            final String userId,
            final String ruleSetCategory,
            final String ruleSetCode);
}
