package ch.ti8m.egov.airulegeneration.api;

import ch.ti8m.egov.airulegeneration.generator.RuleGenerator;
import ch.ti8m.egov.airulegeneration.modification.RuleModificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/validation/v1")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RuleModificationController {

    private final RuleGenerator ruleGenerator;
    private final RuleModificationService ruleModificationService;

    @PostMapping("/modifications/rules")
    public List<Object> createRule(
            @RequestParam("class") final String className,
            @RequestBody final String ruleDescription
    ) {
        return ruleGenerator.generateRule(className, ruleDescription);
    }

    @PostMapping("/modifications/rules/apply")
    public void applyRule(
            @RequestParam("class") final String className,
            @RequestParam("field") final String fieldName,
            @RequestBody final RuleDto ruleDto
    ) {
        ruleModificationService.applyModification(className, fieldName, ruleDto.rule());
    }

}
