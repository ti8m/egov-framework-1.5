package ch.ti8m.egov.framework.validation.command.proxy;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CommandHandlerProxyFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public CommandHandlerProxy getCommandHandlerProxy(final Class<? extends CommandHandlerProxy> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CommandHandlerProxyFactory.applicationContext = applicationContext;
    }
}
