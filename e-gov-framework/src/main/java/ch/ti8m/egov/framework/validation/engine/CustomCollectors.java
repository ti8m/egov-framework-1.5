package ch.ti8m.egov.framework.validation.engine;

import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class CustomCollectors {

    private CustomCollectors() {
    }

    public static <T> Collector<T, ?, T> toSingleton(final String action, final String aggregateState, final String userId) {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.isEmpty()) {
                        throw new ValidationException("no applicable ruleset found for action: " + action + " and aggregateState: " + aggregateState + " and userId: " + userId);
                    } else if (list.size() > 1) {
                        throw new ValidationException("no applicable ruleset found for action: " + action + " and aggregateState: " + aggregateState + " and userId: " + userId + "(ruleset selection ambiguous, found " + list.size() + " rulesets)");
                    }
                    return list.get(0);
                }
        );
    }

}
