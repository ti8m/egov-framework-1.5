package ch.ti8m.egov.testbase.tools;

import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.test.context.support.DefaultActiveProfilesResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergingActiveProfilesResolver implements ActiveProfilesResolver {

    private final DefaultActiveProfilesResolver defaultResolver = new DefaultActiveProfilesResolver();

    @Override
    public String[] resolve(Class<?> testClass) {
        String[] hardcodedProfiles = defaultResolver.resolve(testClass);
        String systemProfiles = System.getProperty("spring.profiles.active");

        List<String> result = new ArrayList<>(Arrays.asList(hardcodedProfiles));
        if (systemProfiles != null && !systemProfiles.isEmpty()) {
            for (String profile : systemProfiles.split(",")) {
                String trimmed = profile.trim();
                if (!trimmed.isEmpty() && !result.contains(trimmed)) {
                    result.add(trimmed);
                }
            }
        }
        return result.toArray(new String[0]);
    }
}
