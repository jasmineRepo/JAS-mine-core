/* (C) Copyright 2026, by Ross Richardson
 * Tests model-defined parameter rules, validation and the metadata available to a GUI.
 * @author ross richardson
 */
package microsim.parameter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ParameterConstraintsTest {
    @Test
    void integerBoundsAreInclusiveAndRemainExactInMetadata() {
        var rule = ParameterConstraints.Rule.integerRange(9007199254740993L, Long.MAX_VALUE, "Invalid seed");

        assertTrue(rule.accepts("9007199254740993"));
        assertTrue(rule.accepts(Long.MAX_VALUE));
        assertFalse(rule.accepts("9007199254740992"));
        assertFalse(rule.accepts("9223372036854775808"));
        assertFalse(rule.accepts("9007199254740993.5"));
        assertFalse(rule.accepts("unknown"));
        assertEquals(Map.of("minimum", "9007199254740993", "maximum", "9223372036854775807",
                "integer", true, "message", "Invalid seed"), rule.metadata());
    }

    @Test
    void reportsEachInvalidSuppliedValueWithTheModelsMessage() {
        ParameterConstraints.Provider model = () -> Map.of(
                "country", ParameterConstraints.Rule.required("UK", "UK required"),
                "endYear", new ParameterConstraints.Rule("2020", null, null, true, "End year precedes start"));
        var rules = model.parameterConstraints();

        assertEquals(Map.of("country", "UK required", "endYear", "End year precedes start"),
                ParameterConstraints.violations(rules, Map.of("country", "IT", "endYear", 2019)));
        assertDoesNotThrow(() -> ParameterConstraints.validate(rules, Map.of("country", "UK", "endYear", 2030)));
        assertEquals(Map.of("requiredValue", "UK", "integer", false, "message", "UK required"),
                rules.get("country").metadata());
    }

    @Test
    void validatesPartialUpdatesUsingOnlyTheSuppliedValues() {
        var rules = Map.of("country", ParameterConstraints.Rule.required("UK", "UK required"),
                "endYear", ParameterConstraints.Rule.integerRange(2019, 2026, "Invalid year"));
        assertDoesNotThrow(() -> ParameterConstraints.validate(rules, Map.of("endYear", 2026)));
        var error = assertThrows(IllegalArgumentException.class,
                () -> ParameterConstraints.validate(rules, Map.of("endYear", 2027)));
        assertEquals("Invalid year", error.getMessage());
    }
}
