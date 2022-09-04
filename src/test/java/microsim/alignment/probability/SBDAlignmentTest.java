package microsim.alignment.probability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SBDAlignmentTest {

    @Test
    void testGenerateSortingVariable() {
        double[] actualGenerateSortingVariableResult = (new SBDAlignment<>())
            .generateSortingVariable(new double[]{10.0d, 10.0d, 10.0d, 10.0d}, new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(4, actualGenerateSortingVariableResult.length);
        assertEquals(0.0d, actualGenerateSortingVariableResult[0]);
        assertEquals(0.0d, actualGenerateSortingVariableResult[1]);
        assertEquals(0.0d, actualGenerateSortingVariableResult[2]);
        assertEquals(0.0d, actualGenerateSortingVariableResult[3]);
    }

    @Test
    void testGenerateSortingVariable2() {
        assertThrows(NullPointerException.class, () -> (new SBDAlignment<>()).generateSortingVariable(null, null));
    }


    @Test
    void testGenerateSortingVariable3() {
        assertEquals(0, (new SBDAlignment<>()).generateSortingVariable(new double[]{},
            new double[]{10.0d, 10.0d, 10.0d, 10.0d}).length);
    }

    @Test
    void testGenerateSortingVariable4() {
        assertThrows(NullPointerException.class,
            () -> (new SBDAlignment<>()).generateSortingVariable(new double[]{10.0d, 10.0d, 10.0d, 10.0d}, null));
    }
}

