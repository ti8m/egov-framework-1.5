package ch.ti8m.egov.framework.iam.persistence.model.generation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ruleset {

    String code();

    String action();

    String description() default "";

    String category() default "";

    int ruleSetPriority() default 0;

}
