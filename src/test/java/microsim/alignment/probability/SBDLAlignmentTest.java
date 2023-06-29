package microsim.alignment.probability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SBDLAlignmentTest {

    @Test
    void testGenerateSortingVariable() {
        double[] actualGenerateSortingVariableResult = (new SBDLAlignment<>())
            .generateSortingVariable(new double[]{10.0d, 10.0d, 10.0d, 10.0d}, new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(4, actualGenerateSortingVariableResult.length);
        assertEquals(Double.NaN, actualGenerateSortingVariableResult[0]);
        assertEquals(Double.NaN, actualGenerateSortingVariableResult[1]);
        assertEquals(Double.NaN, actualGenerateSortingVariableResult[2]);
        assertEquals(Double.NaN, actualGenerateSortingVariableResult[3]);
    }

    @Test
    void testGenerateSortingVariable2() {
        assertThrows(NullPointerException.class, () -> (new SBDLAlignment<>()).generateSortingVariable(null, null));
    }

    @Test
    void testGenerateSortingVariable4() {
        assertThrows(NullPointerException.class,
            () -> (new SBDLAlignment<>()).generateSortingVariable(new double[]{10.0d, 10.0d, 10.0d, 10.0d}, null));
    }
}

