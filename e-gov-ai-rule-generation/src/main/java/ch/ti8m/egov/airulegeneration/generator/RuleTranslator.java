package ch.ti8m.egov.airulegeneration.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RuleTranslator {

    private final ObjectMapper objectMapper;

    public List<Object> translateToRule(final String jsonRule) {
        try {
            final String cleanJsonRule = jsonRule.replace("```json", "").replace("```", "");
            final Map<String, Object> ruleMap = objectMapper.readValue(cleanJsonRule, new TypeReference<>() {
            });
            final StringBuilder ruleBuilder = new StringBuilder();
            mapToLisp(ruleBuilder, ruleMap, false);
            return objectMapper.readValue(ruleBuilder.toString(), new TypeReference<>() {
            });
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void mapToLisp(final StringBuilder ruleBuilder, final Object subPart, final boolean isNegated) {
        if (subPart instanceof final Map<?, ?> mapSubPart) {
            if (!isNegated) {
                ruleBuilder.append("[");
            }
            final Iterator<? extends Map.Entry<?, ?>> iterator = mapSubPart.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<?, ?> entry = iterator.next();
                if (isNegated) {
                    ruleBuilder.append(entry.getKey()).append("\", ");
                } else if ("NOT".equals(entry.getKey())) {
                    ruleBuilder.append("\"").append(entry.getKey()).append(" ");
                } else {
                    ruleBuilder.append("\"").append(entry.getKey()).append("\", ");
                }
                mapToLisp(ruleBuilder, entry.getValue(), "NOT".equals(entry.getKey()));
                if (iterator.hasNext()) {
                    ruleBuilder.append(", ");
                }
            }
            if (!isNegated) {
                ruleBuilder.append("]");
            }
        } else if (subPart instanceof final List<?> listSubPart) {
            final Iterator<?> iterator = listSubPart.iterator();
            while (iterator.hasNext()) {
                mapToLisp(ruleBuilder, iterator.next(), false);
                if (iterator.hasNext()) {
                    ruleBuilder.append(", ");
                }
            }
        } else {
            if (subPart instanceof String) {
                ruleBuilder.append("\"").append(subPart).append("\"");
            } else {
                ruleBuilder.append(subPart);
            }
        }
    }

}
