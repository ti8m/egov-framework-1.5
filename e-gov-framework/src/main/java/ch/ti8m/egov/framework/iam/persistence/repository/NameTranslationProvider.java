package ch.ti8m.egov.framework.iam.persistence.repository;

import ch.ti8m.egov.framework.persistence.util.NameTranslationComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NameTranslationProvider {

    private final NameTranslationComponent nameTranslationComponent;

    @Autowired
    public NameTranslationProvider(NameTranslationComponent nameTranslationComponent) {
        this.nameTranslationComponent = nameTranslationComponent;
    }

    public String getFamilyIdFieldName() {
        return nameTranslationComponent.getTranslatedName("FamilyId");
    }

    public String getUserIdFieldName() {
        return nameTranslationComponent.getTranslatedName("Userid");
    }

    public String getValidFromFieldName() {
        return nameTranslationComponent.getTranslatedName("ValidFrom");
    }

    public String getValidToFieldName() {
        return nameTranslationComponent.getTranslatedName("ValidTo");
    }

    public String getEntityTitleFieldName() {
        return nameTranslationComponent.getTranslatedName("EntityTitle");
    }

    public String getOperationFieldName() {
        return nameTranslationComponent.getTranslatedName("Operation");
    }

    public String getRoleNameFieldName() {
        return nameTranslationComponent.getTranslatedName("RoleName");
    }

    public String getRuleSetCodeFieldName() {
        return nameTranslationComponent.getTranslatedName("RuleSetCode");
    }

    public String getRuleSetCategoryFieldName() {
        return nameTranslationComponent.getTranslatedName("Category");
    }

    public String getRuleSetPriorityFieldName() {
        return nameTranslationComponent.getTranslatedName("RuleSetPriority");
    }

    public String getValidationEntityFieldName() {
        return nameTranslationComponent.getTranslatedName("ValidationEntity");
    }

    public String getAllowedForAllStatesFieldName() {
        return nameTranslationComponent.getTranslatedName("AllowedForAllStates");
    }

    public String getAllowedForAllRolesFieldName() {
        return nameTranslationComponent.getTranslatedName("AllowedForAllRoles");
    }

    public String getActionFieldName() {
        return nameTranslationComponent.getTranslatedName("Action");
    }

    public String getStateFieldName() {
        return nameTranslationComponent.getTranslatedName("State");
    }

    public String getSqlQueryFieldName() {
        return nameTranslationComponent.getTranslatedName("SqlQuery");
    }
}
