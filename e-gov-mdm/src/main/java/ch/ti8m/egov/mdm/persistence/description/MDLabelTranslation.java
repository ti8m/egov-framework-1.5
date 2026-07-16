package ch.ti8m.egov.mdm.persistence.description;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface MDLabelTranslation {
    String GERMAN = "de";
    String FRENCH = "fr";
    String ITALIAN = "it";

    String language();

    String name() default "";

    String description() default "";
}
