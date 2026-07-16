package ch.ti8m.egov.framework.iam.persistence.repository;

import ch.ti8m.egov.framework.deployconfig.DatabaseConfigurationService;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.Condition;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Component
@Slf4j
public class RuleSetRepository {

    private static final String USER_ID = "userId";
    private static final String ACTION = "action";
    private static final String AGGREGATE_STATE = "aggregateState";
    private static final String RULE_SET_CODE = "ruleSetCode";
    private static final String RULE_SET_CATEGORY = "ruleSetCategory";
    private static final String RULE_SET_ID = "ruleSetId";
    private static final String RULE_SET_IDS = "ruleSetIds";
    private final DatabaseConfigurationService databaseConfigurationService;
    private final NameTranslationProvider nameTranslationProvider;
    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void setUp() {
        log.info("databaseConfigurationService initialized! Dialect Current Date Syntax = {}",
                databaseConfigurationService.getCurrentDateString());
    }

    // GET_APPLICABLE_RULESETS
    private String getApplicableRulesetSql() {
        return String.format(
                "SELECT EGOV_IAM_RULE_SET.*"
                        + " FROM EGOV_IAM_RULE_SET"
                        + " LEFT JOIN EGOV_IAM_CONDITION ON EGOV_IAM_CONDITION.rulesetid = EGOV_IAM_RULE_SET.id"
                        + " LEFT JOIN EGOV_IAM_CONDITION_ALLOWED_FOR_STATE ON EGOV_IAM_CONDITION_ALLOWED_FOR_STATE.conditionid = EGOV_IAM_CONDITION.id"
                        + " LEFT JOIN EGOV_IAM_CONDITION_ALLOWED_FOR_ROLE ON EGOV_IAM_CONDITION_ALLOWED_FOR_ROLE.conditionid = EGOV_IAM_CONDITION.id"
                        + " LEFT JOIN EGOV_IAM_ROLE ON EGOV_IAM_ROLE.id = EGOV_IAM_CONDITION_ALLOWED_FOR_ROLE.roleid"
                        + " LEFT JOIN EGOV_IAM_ROLE_MEMBERSHIP ON EGOV_IAM_ROLE_MEMBERSHIP.roleid = EGOV_IAM_ROLE.id"
                        + " WHERE ("
                        + " EGOV_IAM_CONDITION." + nameTranslationProvider.getAllowedForAllStatesFieldName() + " = %1$s"
                        + " OR"
                        + " EGOV_IAM_CONDITION_ALLOWED_FOR_STATE." + nameTranslationProvider.getStateFieldName() + " = :%2$s"
                        + " ) AND ("
                        + " EGOV_IAM_CONDITION." + nameTranslationProvider.getAllowedForAllRolesFieldName() + " = %1$s"
                        + " OR ("
                        + " EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getUserIdFieldName() + " = :%3$s"
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidFromFieldName() + " <= %5$s"
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidToFieldName() + " >= %5$s"
                        + " )"
                        + " ) AND ("
                        + " EGOV_IAM_CONDITION." + nameTranslationProvider.getActionFieldName() + " = :%4$s"
                        + " )"
                        + " ORDER BY EGOV_IAM_RULE_SET." + nameTranslationProvider.getRuleSetPriorityFieldName() + " DESC",
                databaseConfigurationService.getTrueStatement(),
                AGGREGATE_STATE,
                USER_ID,
                ACTION,
                databaseConfigurationService.getCurrentDateString()
        );
    }

    //GET_APPLICABLE_RULESETS_ADMIN
    private String getApplicableRulesetsAdminSql() {
        return String.format(
                "SELECT EGOV_IAM_RULE_SET.*"
                        + " FROM EGOV_IAM_RULE_SET"
                        + " LEFT JOIN EGOV_IAM_CONDITION ON EGOV_IAM_CONDITION.rulesetid = EGOV_IAM_RULE_SET.id"
                        + " LEFT JOIN EGOV_IAM_CONDITION_ALLOWED_FOR_STATE ON EGOV_IAM_CONDITION_ALLOWED_FOR_STATE.conditionid = EGOV_IAM_CONDITION.id"
                        + " WHERE ("
                        + " EGOV_IAM_CONDITION." + nameTranslationProvider.getAllowedForAllStatesFieldName() + " = %1$s"
                        + " OR"
                        + " EGOV_IAM_CONDITION_ALLOWED_FOR_STATE." + nameTranslationProvider.getStateFieldName() + " = :%2$s"
                        + " ) AND ("
                        + " EGOV_IAM_CONDITION." + nameTranslationProvider.getActionFieldName() + " = :%3$s"
                        + " )"
                        + " ORDER BY LEN(EGOV_IAM_RULE_SET." + nameTranslationProvider.getValidationEntityFieldName() + ") DESC",
                databaseConfigurationService.getTrueStatement(),
                AGGREGATE_STATE,
                ACTION
        );
    }

    // If multiple rulesets are found for an action, the one with the longest ValidationEntity will be used.
    // ...
    //GET_APPLICABLE_CONDITIONS
    private String getApplicableConditionsSql() {
        return String.format(
                "SELECT EGOV_IAM_CONDITION.*"
                        + " FROM EGOV_IAM_RULE_SET"
                        + " LEFT JOIN EGOV_IAM_CONDITION ON EGOV_IAM_CONDITION.rulesetid = EGOV_IAM_RULE_SET.id "
                        + " LEFT JOIN EGOV_IAM_CONDITION_ALLOWED_FOR_STATE ON EGOV_IAM_CONDITION_ALLOWED_FOR_STATE.conditionid = EGOV_IAM_CONDITION.id "
                        + " LEFT JOIN EGOV_IAM_CONDITION_ALLOWED_FOR_ROLE ON EGOV_IAM_CONDITION_ALLOWED_FOR_ROLE.conditionid = EGOV_IAM_CONDITION.id "
                        + " LEFT JOIN EGOV_IAM_ROLE ON EGOV_IAM_ROLE.id = EGOV_IAM_CONDITION_ALLOWED_FOR_ROLE.roleid"
                        + " LEFT JOIN EGOV_IAM_ROLE_MEMBERSHIP ON EGOV_IAM_ROLE_MEMBERSHIP.roleid = EGOV_IAM_ROLE.id"
                        + " WHERE ("
                        + " EGOV_IAM_CONDITION." + nameTranslationProvider.getAllowedForAllRolesFieldName() + " = %1$s"
                        + " OR ("
                        + " EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getUserIdFieldName() + " = :%2$s"
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidFromFieldName() + " <= %3$s"
                        + " AND EGOV_IAM_ROLE_MEMBERSHIP." + nameTranslationProvider.getValidToFieldName() + " >= %3$s"
                        + " ))",
                databaseConfigurationService.getTrueStatement(),
                USER_ID,
                databaseConfigurationService.getCurrentDateString()
        );
    }

    // GET_APPLICABLE_CONDITIONS_ADMIN
    private String getApplicableConditionsAdminSql() {
        return "SELECT EGOV_IAM_CONDITION.* FROM EGOV_IAM_CONDITION";
    }

    // FIND_BY_RULE_SET_CODE
    private String getFindByRuleSetCodeSql() {
        return String.format(
                "SELECT EGOV_IAM_RULE_SET.*"
                        + " FROM EGOV_IAM_RULE_SET"
                        + " WHERE EGOV_IAM_RULE_SET." + nameTranslationProvider.getRuleSetCodeFieldName() + " = :%s",
                RULE_SET_CODE
        );
    }

    // FIND_BY_ID
    private String getFindByIdSql() {
        return String.format(
                "SELECT EGOV_IAM_RULE_SET.*"
                        + " FROM EGOV_IAM_RULE_SET"
                        + " WHERE EGOV_IAM_RULE_SET.id = :%s",
                RULE_SET_ID
        );
    }

    // FIND_BY_IDS
    private String getFindByIdsSql() {
        return String.format(
                "SELECT EGOV_IAM_RULE_SET.*"
                        + " FROM EGOV_IAM_RULE_SET"
                        + " WHERE EGOV_IAM_RULE_SET.id IN (:%s)",
                RULE_SET_IDS
        );
    }


    public Optional<RuleSet> getApplicableRuleSet(
            final String userId,
            final String action,
            final Enum<?> aggregateState,
            final boolean isAdmin
    ) {
        final String stateName = aggregateState == null ? "*" : aggregateState.name();
        final Query query;
        if (isAdmin) {
            if (log.isInfoEnabled()) {
                log.info("Admin ({}) access to action {} and state {}",
                        userId, action, stateName);
            }
            query = entityManager.createNativeQuery(getApplicableRulesetsAdminSql(), RuleSet.class);
        } else {
            query = entityManager.createNativeQuery(getApplicableRulesetSql(), RuleSet.class);
            query.setParameter(USER_ID, userId);
        }
        query.setParameter(ACTION, action);
        query.setParameter(AGGREGATE_STATE, stateName);
        return query.getResultList().stream().findFirst();
    }

    public Optional<RuleSet> findByRuleSetCode(final String ruleSetCode) {
        final Query query = entityManager.createNativeQuery(getFindByRuleSetCodeSql(), RuleSet.class);
        query.setParameter(RULE_SET_CODE, ruleSetCode);
        return query.getResultList().stream().findFirst();
    }

    public Optional<RuleSet> findById(final int ruleSetId) {
        final Query query = entityManager.createNativeQuery(getFindByIdSql(), RuleSet.class);
        query.setParameter(RULE_SET_ID, ruleSetId);
        return query.getResultList().stream().findFirst();
    }

    public List<RuleSet> findByIds(final List<Integer> ids) {
        final Query query = entityManager.createNativeQuery(getFindByIdsSql(), RuleSet.class);
        query.setParameter(RULE_SET_IDS, ids);
        return query.getResultList();
    }

    public List<Condition> getApplicableConditions(
            final String userId,
            final String ruleSetCategory,
            final String ruleSetCode,
            final boolean isAdmin
    ) {
        final Query query;
        if (isAdmin) {
            if (log.isInfoEnabled()) {
                log.info("Admin ({}) access to applicable conditions with ruleSetCategory ({}) and ruleSetCode ({})",
                        userId, ruleSetCategory, ruleSetCode);
            }
            query = entityManager.createNativeQuery(getApplicableConditionsAdminSql(), Condition.class);
        } else {
            String sql = getApplicableConditionsSql();
            final Map<String, Object> parameters = new HashMap<>();
            parameters.put(USER_ID, userId);

            if (ruleSetCategory != null) {
                sql += " AND EGOV_IAM_RULE_SET." + nameTranslationProvider.getRuleSetCategoryFieldName() + " = :" + RULE_SET_CATEGORY;
                parameters.put(RULE_SET_CATEGORY, ruleSetCategory);
            }
            if (ruleSetCode != null) {
                sql += " AND EGOV_IAM_RULE_SET." + nameTranslationProvider.getRuleSetCodeFieldName() + " = :" + RULE_SET_CODE;
                parameters.put(RULE_SET_CODE, ruleSetCode);
            }

            query = entityManager.createNativeQuery(sql, Condition.class);
            parameters.forEach(query::setParameter);
        }
        return query.getResultList();
    }
}