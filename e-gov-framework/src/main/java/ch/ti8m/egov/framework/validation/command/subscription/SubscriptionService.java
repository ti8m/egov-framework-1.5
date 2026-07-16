package ch.ti8m.egov.framework.validation.command.subscription;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.validation.command.Command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final ApplicationEventPublisher eventPublisher;
    @Getter
    private final Map<String, Set<Subscriber>> subscriptions = new HashMap<>();

    public void runSubscriptions(final Command command) {
        Stream.of(command.getAction(), command.getOriginalAction())
                .filter(subscriptions::containsKey)
                .map(subscriptions::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
                .forEach(subscriber -> eventPublisher.publishEvent(new SubscriptionDistributionService.SubscriptionEvent(
                        this,
                        DataHolder.getUserId(),
                        command,
                        subscriber
                )));
    }

    public void subscribe(final Subscriber subscriber, final String action) {
        subscribe(subscriber, List.of(action));
    }

    public void subscribe(final Subscriber subscriber, final List<String> actions) {
        SubscriptionService.log.info("[SUBSCRIPTION] Registered new subscriber: {}", subscriber.getClass().getName());
        subscriptions.forEach((action, subscribers) -> subscribers
                .forEach(registeredSubscriber -> {
                    if (registeredSubscriber.getClass().getName().equals(subscriber.getClass().getName())) {
                        subscribers.remove(registeredSubscriber);
                    }
                })
        );

        actions.forEach(action -> {
            if (!subscriptions.containsKey(action)) {
                subscriptions.put(action, new CopyOnWriteArraySet<>());
            }
            subscriptions.get(action).add(subscriber);
        });
    }

    public Map<String, Set<Subscriber>> getActiveSubscriptions() {
        return subscriptions;
    }

    public void unsubscribe(final List<String> actions, final Subscriber subscriber) {
        subscriptions.forEach((key, value) -> {
            if (actions.contains(key)) {
                value.remove(subscriber);
            }
        });
    }
}
