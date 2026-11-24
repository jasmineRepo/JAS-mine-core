/*
 * (C) Copyright 2026, by Ross Richardson
 *
 * Checks the runtime editing metadata exposed to consumers of GUIparameter.
 * @author ross richardson
 */
package microsim.annotation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GUIparameterTest {
    @GUIparameter(description = "Tax rate")
    private double taxRate;

    @GUIparameter(description = "Initial population", runtimeModifiable = false)
    private int population;

    @Test
    void existingAnnotationsAllowRuntimeEditingByDefault() throws NoSuchFieldException {
        var field = GUIparameterTest.class.getDeclaredField("taxRate");
        assertTrue(field.getAnnotation(GUIparameter.class).runtimeModifiable());
    }

    @Test
    void modelCanMarkAParameterAsFixedAfterBuild() throws NoSuchFieldException {
        var field = GUIparameterTest.class.getDeclaredField("population");
        assertFalse(field.getAnnotation(GUIparameter.class).runtimeModifiable());
    }
}
