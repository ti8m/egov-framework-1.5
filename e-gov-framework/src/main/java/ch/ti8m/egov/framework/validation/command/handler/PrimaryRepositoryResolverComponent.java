package ch.ti8m.egov.framework.validation.command.handler;

import ch.ti8m.egov.framework.persistence.base.PrimaryRepository;
import ch.ti8m.egov.framework.validation.command.executor.BaseExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;

@Component
@Slf4j
public class PrimaryRepositoryResolverComponent {

    public <T extends BaseExecutor> boolean isPrimaryRepositoryPresent(final T executor) {
        for (final Field field : ClassUtils.getUserClass(executor.getClass()).getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(PrimaryRepository.class)) {
                return true;
            }
        }
        return false;
    }

}
