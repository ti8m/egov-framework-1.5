package ch.ti8m.egov.mdm.persistence.description;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {
        ElementType.FIELD,
        ElementType.TYPE
})
public @interface MDLabel {
    String code();

    String defaultValue() default "";

    MDLabelTranslation[] translations() default {};
}
