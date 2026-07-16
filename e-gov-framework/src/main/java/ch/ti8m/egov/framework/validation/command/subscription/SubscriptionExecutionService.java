package ch.ti8m.egov.framework.validation.command.subscription;

import ch.ti8m.egov.framework.validation.command.Command;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionExecutionService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute(
            final Subscriber subscriber,
            final Command command,
            final String userId
    ) {
        subscriber.execute(command, userId);
    }

}
