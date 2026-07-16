package ch.ti8m.egov.mdm.persistence;

import ch.ti8m.egov.framework.exceptionhandling.model.EGovException;
import ch.ti8m.egov.framework.persistence.util.ReflectionUtils;
import ch.ti8m.egov.mdm.api.abstraction.Masterdata;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.persistence.description.MDLabel;
import ch.ti8m.egov.mdm.persistence.description.MDVocabulary;
import ch.ti8m.egov.mdm.persistence.entity.FieldDefinition;
import ch.ti8m.egov.mdm.persistence.entity.FieldDefinitionLn;
import ch.ti8m.egov.mdm.persistence.entity.LanguageDefinition;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import ch.ti8m.egov.mdm.persistence.entity.VocabularyLn;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MasterdataVocabularyScanner {

    private final VocabularyRepository vocabularyRepository;

    @PostConstruct
    public void init() {
        vocabularyRepository.deactivatePermissions();
        findMasterdataClasses("ch.ti8m").forEach(masterdataClass -> {
            final MDVocabulary mdVocabulary = masterdataClass.getAnnotation(MDVocabulary.class);
            vocabularyRepository.findOneBy("code", mdVocabulary.code()).ifPresentOrElse(
                    vocabulary -> log.info("Found existing vocabulary with code [{}]. Skipping further processing.", mdVocabulary.code()),
                    () -> {
                        log.info("Creating new vocabulary with code [{}]", mdVocabulary.code());
                        final Vocabulary vocabulary = Vocabulary.builder()
                                .code(mdVocabulary.code())
                                .modifiable(mdVocabulary.isModifiable())
                                .sortable(mdVocabulary.isSortable())
                                .build();
                        addLanguages(mdVocabulary, vocabulary);
                        addTranslations(mdVocabulary, vocabulary);
                        addFields(masterdataClass, vocabulary);

                        vocabularyRepository.saveWithTx(vocabulary);
                    }
            );
        });
        vocabularyRepository.activatePermissions();
    }

    private void addLanguages(final MDVocabulary mdVocabulary, final Vocabulary vocabulary) {
        Arrays.stream(mdVocabulary.availableLanguages()).map(languageCode -> LanguageDefinition.builder()
                        .vocabulary(vocabulary)
                        .languageCode(languageCode)
                        .build())
                .forEach(languageDefinition -> vocabulary.getLanguages().add(languageDefinition));
    }

    private void addTranslations(final MDVocabulary mdVocabulary, final Vocabulary vocabulary) {
        Arrays.stream(mdVocabulary.translations()).map(mdLabelTranslation -> VocabularyLn.builder()
                        .vocabulary(vocabulary)
                        .languageCode(mdLabelTranslation.language())
                        .name(mdLabelTranslation.name())
                        .description(mdLabelTranslation.description())
                        .build())
                .forEach(vocabularyLn -> vocabulary.getLocalizations().add(vocabularyLn));
    }

    private void addFields(final Class<? extends Masterdata> masterdataClass, final Vocabulary vocabulary) {
        ReflectionUtils.getAllEntityFields(masterdataClass)
                .stream()
                .filter(field -> field.isAnnotationPresent(MDLabel.class))
                .map(field -> {
                    final MDLabel mdLabel = field.getAnnotation(MDLabel.class);
                    final FieldDefinition fieldDefinition = FieldDefinition.builder()
                            .vocabulary(vocabulary)
                            .code(mdLabel.code())
                            .type(field.getGenericType().getTypeName())
                            .defaultValue(mdLabel.defaultValue())
                            .build();
                    addFieldTranslations(mdLabel, fieldDefinition);
                    return fieldDefinition;
                })
                .forEach(fieldDefinition -> vocabulary.getFields().add(fieldDefinition));
    }

    private void addFieldTranslations(final MDLabel mdLabel, final FieldDefinition fieldDefinition) {
        Arrays.stream(mdLabel.translations()).map(mdLabelTranslation -> FieldDefinitionLn.builder()
                        .field(fieldDefinition)
                        .languageCode(mdLabelTranslation.language())
                        .name(mdLabelTranslation.name())
                        .build())
                .forEach(fieldDefinitionLn -> fieldDefinition.getLocalizations().add(fieldDefinitionLn));
    }

    private Set<Class<? extends Masterdata>> findMasterdataClasses(final String basePackage) {
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Masterdata.class));
        return scanner.findCandidateComponents(basePackage)
                .stream()
                .map(this::loadClass)
                .collect(Collectors.toSet());
    }

    private Class<? extends Masterdata> loadClass(final BeanDefinition beanDefinition) {
        try {
            return (Class<? extends Masterdata>) Class.forName(beanDefinition.getBeanClassName());
        } catch (final ClassNotFoundException e) {
            throw new EGovException(e);
        }
    }

}
