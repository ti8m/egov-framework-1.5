package ch.ti8m.egov.airulegeneration.modification;

import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;
import ch.ti8m.egov.framework.iam.persistence.repository.RuleSetRepository;
import ch.ti8m.egov.framework.iam.siteimporter.SiteImporterRuleSetRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RuleModificationService {

    private final RuleSetRepository ruleSetRepository;
    private final SiteImporterRuleSetRepository siteImporterRuleSetRepository;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Transactional
    public void applyModification(
            final String className,
            final String fieldName,
            final List<Object> rule
    ) {
        log.info("Applying rule to class [{}] on field [{}]: {}", className, fieldName, rule);
        final RuleSet ruleSet = ruleSetRepository.findByRuleSetCode("CREATE_GESCHEAFT").orElseThrow();

        final Iterator<String> iterator = Arrays.asList(fieldName.split("\\.")).iterator();

        updateRuleSet(ruleSet.getValidationEntity(), iterator, rule);
        siteImporterRuleSetRepository.update(ruleSet);
    }

    private void updateRuleSet(final ValidationEntity validationEntity, final Iterator<String> fieldNameIterator, final List<Object> rule) {
        final String currentFielt = fieldNameIterator.next();
        if (fieldNameIterator.hasNext()) {
            updateRuleSet(validationEntity.get(currentFielt), fieldNameIterator, rule);
        } else {
            validationEntity.get(currentFielt).setValidation(rule);
        }
    }

}
