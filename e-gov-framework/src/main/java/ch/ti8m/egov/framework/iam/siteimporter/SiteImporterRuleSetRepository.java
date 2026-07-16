package ch.ti8m.egov.framework.iam.siteimporter;

import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SiteImporterRuleSetRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void deleteAll() {
        entityManager.createNativeQuery("SELECT * FROM EGOV_IAM_RuleSet", RuleSet.class)
                .getResultList()
                .forEach(entityManager::remove);
    }

    public void saveAll(final List<RuleSet> ruleSets) {
        ruleSets.forEach(entityManager::merge);
    }

    public void saveOrUpdateAll(final List<RuleSet> ruleSets) {
        ruleSets.forEach(ruleSet -> {
            if (ruleSet.getId() == null) {
                entityManager.persist(ruleSet);
            } else {
                entityManager.merge(ruleSet);
            }
        });
    }

    public List<RuleSet> findAll() {
        return entityManager.createNativeQuery("SELECT * FROM EGOV_IAM_RuleSet", RuleSet.class)
                .getResultList();
    }

    public void update(final RuleSet ruleSet) {
        entityManager.merge(ruleSet);
    }

}
