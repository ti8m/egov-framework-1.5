package ch.ti8m.egov.framework.validation.command.subscription;

import ch.ti8m.egov.framework.validation.command.Command;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    ApplicationEventPublisher applicationEventPublisher;
    @InjectMocks
    SubscriptionService subscriptionService;

    @Mock
    Subscriber subscriber;
    @Mock
    Subscriber subscriber2;
    @Captor
    ArgumentCaptor<SubscriptionDistributionService.SubscriptionEvent> eventCaptor;

    @AfterEach
    void tearDown() {
        subscriptionService.getActiveSubscriptions().clear();
    }

    @Test
    void whenSubscriberOnAction_thenApplicationEventPublished() {
        final String action = "action";
        final String originalAction = "action_original";
        subscriptionService.getSubscriptions().put(
                action,
                Set.of(subscriber)
        );

        subscriptionService.runSubscriptions(Command.builder().action(action).originalAction(originalAction).build());

        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(eventCaptor.capture());
        Assertions.assertThat(eventCaptor.getValue().getSubscriber()).isEqualTo(subscriber);
    }

    @Test
    void whenSubscriberOnOriginalAction_thenApplicationEventPublished() {
        final String action = "action";
        final String originalAction = "action_original";
        subscriptionService.getSubscriptions().put(
                originalAction,
                Set.of(subscriber)
        );

        subscriptionService.runSubscriptions(Command.builder().action(action).originalAction(originalAction).build());

        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(eventCaptor.capture());
        Assertions.assertThat(eventCaptor.getValue().getSubscriber()).isEqualTo(subscriber);
    }

    @Test
    void whenSubscriberOnBothActionAndOriginalAction_thenOneApplicationEventPublished() {
        final String action = "action";
        final String originalAction = "action_original";
        subscriptionService.getSubscriptions().put(
                action,
                Set.of(subscriber)
        );
        subscriptionService.getSubscriptions().put(
                originalAction,
                Set.of(subscriber)
        );

        subscriptionService.runSubscriptions(Command.builder().action(action).originalAction(originalAction).build());

        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(eventCaptor.capture());
        Assertions.assertThat(eventCaptor.getValue().getSubscriber()).isEqualTo(subscriber);
    }

    @Test
    void whenDifferentSubscribersOnBothActionAndOriginalAction_thenTwoApplicationEventPublished() {
        final String action = "action";
        final String originalAction = "action_original";
        subscriptionService.getSubscriptions().put(
                action,
                Set.of(subscriber)
        );
        subscriptionService.getSubscriptions().put(
                originalAction,
                Set.of(subscriber2)
        );

        subscriptionService.runSubscriptions(Command.builder().action(action).originalAction(originalAction).build());

        Mockito.verify(applicationEventPublisher, Mockito.times(2)).publishEvent(eventCaptor.capture());
        Assertions.assertWith(eventCaptor.getAllValues(), subscriptionEvents ->
                Assertions.assertThat(subscriptionEvents.stream()
                                .map(SubscriptionDistributionService.SubscriptionEvent::getSubscriber)
                                .toList())
                        .containsExactlyInAnyOrder(subscriber, subscriber2));
    }

}