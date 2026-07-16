package ch.ti8m.egov.framework.validation.command.handler;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CommandHandlerFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public CommandHandler getCommandHandler(final Class<? extends CommandHandler> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CommandHandlerFactory.applicationContext = applicationContext;
    }
}
