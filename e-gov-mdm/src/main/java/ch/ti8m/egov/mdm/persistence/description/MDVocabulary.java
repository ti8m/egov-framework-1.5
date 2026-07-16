package ch.ti8m.egov.mdm.persistence.description;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MDVocabulary {
    String code();

    MDLabelTranslation[] translations() default {};

    String[] availableLanguages() default {MDLabelTranslation.GERMAN, MDLabelTranslation.FRENCH, MDLabelTranslation.ITALIAN};

    boolean isModifiable() default false;

    boolean isSortable() default true;
}
