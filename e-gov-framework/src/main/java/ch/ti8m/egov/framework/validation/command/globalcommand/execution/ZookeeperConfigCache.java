package ch.ti8m.egov.framework.validation.command.globalcommand.execution;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ZookeeperConfigCache {

    @Getter
    private static final Map<String, String> actionUrlMap = new ConcurrentHashMap<>();

    public void setConfig(final String action, final String url) {
        actionUrlMap.put(getCleanedAction(action), url);
    }

    public Optional<String> getUrl(final String action) {
        return Optional.ofNullable(actionUrlMap.get(getCleanedAction(action)));
    }

    public void removeConfig(final String action) {
        actionUrlMap.remove(getCleanedAction(action));
    }

    private String getCleanedAction(final String action) {
        return action.replace("/", "");
    }

}
