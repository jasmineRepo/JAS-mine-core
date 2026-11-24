/* (C) Copyright 2026, by Ross Richardson
 * Declarative parameter constraints shared by model validation and user interfaces.
 * @author ross richardson
 */
package microsim.parameter;

import java.math.BigDecimal;
import java.util.*;

public final class ParameterConstraints {
    private ParameterConstraints() {}

    /** Providers must return definitions without setup, I/O or engine mutation. */
    public interface Provider {
        Map<String, Rule> parameterConstraints();
    }

    /** Decimal strings preserve exact integer limits in JSON and JavaScript. */
    public record Rule(String minimum, String maximum, String requiredValue, boolean integer, String message) {
        public Rule {
            if (minimum != null) new BigDecimal(minimum);
            if (maximum != null) new BigDecimal(maximum);
            Objects.requireNonNull(message);
        }
        public static Rule integerRange(long minimum, long maximum, String message) {
            return new Rule(Long.toString(minimum), Long.toString(maximum), null, true, message);
        }
        public static Rule required(String value, String message) {
            return new Rule(null, null, Objects.requireNonNull(value), false, message);
        }
        public boolean accepts(Object value) {
            if (value == null) return false;
            if (requiredValue != null && !requiredValue.equals(String.valueOf(value))) return false;
            if (minimum != null || maximum != null || integer) {
                try {
                    BigDecimal n = new BigDecimal(String.valueOf(value));
                    if (integer) n.toBigIntegerExact();
                    if (minimum != null && n.compareTo(new BigDecimal(minimum)) < 0) return false;
                    if (maximum != null && n.compareTo(new BigDecimal(maximum)) > 0) return false;
                } catch (NumberFormatException | ArithmeticException e) { return false; }
            }
            return true;
        }
        public Map<String, Object> metadata() {
            Map<String, Object> result = new LinkedHashMap<>();
            if (minimum != null) result.put("minimum", minimum);
            if (maximum != null) result.put("maximum", maximum);
            if (requiredValue != null) result.put("requiredValue", requiredValue);
            result.put("integer", integer);
            result.put("message", message);
            return result;
        }
    }

    public static Map<String, String> violations(Map<String, Rule> rules, Map<String, Object> values) {
        Map<String, String> errors = new LinkedHashMap<>();
        rules.forEach((name, rule) -> {
            if (values.containsKey(name) && !rule.accepts(values.get(name))) errors.put(name, rule.message());
        });
        return errors;
    }
    public static void validate(Map<String, Rule> rules, Map<String, Object> values) {
        var errors = violations(rules, values);
        if (!errors.isEmpty()) throw new IllegalArgumentException(String.join("; ", errors.values()));
    }
}
