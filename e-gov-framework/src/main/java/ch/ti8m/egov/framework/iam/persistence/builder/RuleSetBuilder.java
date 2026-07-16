package ch.ti8m.egov.framework.iam.persistence.builder;


import ch.ti8m.egov.framework.iam.persistence.model.ruleset.RuleSet;
import ch.ti8m.egov.framework.iam.persistence.model.ruleset.ValidationEntity;

public final class RuleSetBuilder {

    private RuleSetBuilder() {

    }

    public static RuleSetDescriptionBuilder builder() {
        return new RuleSetDescriptionBuilder();
    }

    public static class RuleSetDescriptionBuilder {

        private String description;

        public RuleSetCodeBuilder description(final String description) {
            this.description = description;
            return new RuleSetCodeBuilder();
        }

        public class RuleSetCodeBuilder {

            private String code;

            public RuleSetCategoryBuilder code(final String code) {
                this.code = code;
                return new RuleSetCategoryBuilder();
            }

            public class RuleSetCategoryBuilder {

                private String category;

                public ValidationEntityPartBuilder category(final String category) {
                    this.category = category;
                    return new ValidationEntityPartBuilder();
                }

                public class ValidationEntityPartBuilder {

                    private ValidationEntity validationEntity;

                    public ValidationEntityCompiler validationEntity(final ValidationEntity validationEntity) {
                        this.validationEntity = validationEntity;
                        return new ValidationEntityCompiler();
                    }

                    public class ValidationEntityCompiler {

                        public RuleSet build() {
                            final RuleSet ruleSet = new RuleSet();
                            ruleSet.setDescription(description);
                            ruleSet.setRuleSetCode(code);
                            ruleSet.setCategory(category);
                            ruleSet.setValidationEntity(validationEntity);
                            return ruleSet;
                        }

                    }

                }

            }

        }

    }

}
