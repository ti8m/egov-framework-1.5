package ch.ti8m.egov.framework.iam.components;

import ch.ti8m.egov.framework.deployconfig.DevelopmentConfigurationService;
import ch.ti8m.egov.framework.exceptionhandling.model.ActionNotAllowedException;
import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.exceptionhandling.model.ExceptionCode;
import ch.ti8m.egov.framework.iam.api.java.RuleSetService;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.Condition;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.iam.persistence.repository.RuleSetRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RuleSetServiceImpl implements RuleSetService {

    private final RuleSetRepository ruleSetRepository;

    private final AdminCheckComponent adminCheckComponent;

    private final DevelopmentConfigurationService developmentConfigurationComponent;

    public RuleSetServiceImpl(RuleSetRepository ruleSetRepository, AdminCheckComponent adminCheckComponent, DevelopmentConfigurationService developmentConfigurationComponent) {
        this.ruleSetRepository = ruleSetRepository;
        this.adminCheckComponent = adminCheckComponent;
        this.developmentConfigurationComponent = developmentConfigurationComponent;
    }

    @Override
    public RuleSet getRuleSet(
            final String userId,
            final String action,
            final Enum<?> aggregateState
    ) {
        final RuleSet ruleSet = ruleSetRepository.getApplicableRuleSet(
                userId,
                action,
                aggregateState,
                adminCheckComponent.isAdmin(userId)
        ).orElseThrow(() -> new ActionNotAllowedException(
                action,
                aggregateState,
                userId
        ));

        if (developmentConfigurationComponent.isPermissionLogActivated()) {
            System.out.printf(
                    "Selected Ruleset for User(%d), Action(%s), Aggregate State(%s) is: Id(%d), RuleSetCode(%s)%n",
                    userId,
                    action,
                    aggregateState,
                    ruleSet.getId(),
                    ruleSet.getRuleSetCode()
            );
        }

        return ruleSet;
    }

    @Override
    public RuleSet findByRuleSetCode(final String ruleSetCode) {
        final Optional<RuleSet> ruleSet = ruleSetRepository.findByRuleSetCode(
                ruleSetCode
        );

        return ruleSet.orElseThrow(() -> new EGovException(ExceptionCode.DEFAULT, "Ruleset not found with code " + ruleSetCode));
    }

    @Override
    public Optional<RuleSet> getRuleSetById(final int ruleSetId) {
        return ruleSetRepository.findById(ruleSetId);
    }

    @Override
    public List<RuleSet> getRuleSetsByIds(final List<Integer> ids) {
        return ruleSetRepository.findByIds(ids);
    }

    @Override
    public List<String> getApplicableActions(final String userId, final String ruleSetCategory, final String ruleSetCode) {
        return ruleSetRepository.getApplicableConditions(userId, ruleSetCategory, ruleSetCode, adminCheckComponent.isAdmin(userId))
                .stream().map(Condition::getAction).collect(Collectors.toList());
    }

}
