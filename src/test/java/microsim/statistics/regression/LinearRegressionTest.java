package microsim.statistics.regression;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinearRegressionTest {

    @Test
    void testComputeScore2() {
        assertEquals(0.0d, LinearRegression.computeScore(null, (Object) null));
    }

    @Test
    void testMultiplyCoeffsWithValues() {
        HashMap<String, Double> regCoeffMap = new HashMap<>();
        assertEquals(0.0d, LinearRegression.multiplyCoeffsWithValues(regCoeffMap, new HashMap<>()));
    }

    @Test
    void testMultiplyCoeffsWithValues2() {
        HashMap<String, Double> stringResultDoubleMap = new HashMap<>();
        stringResultDoubleMap.put("Key", 10.0d);
        assertEquals(0.0d, LinearRegression.multiplyCoeffsWithValues(stringResultDoubleMap, new HashMap<>()));
    }

    @Test
    void testMultiplyCoeffsWithValues3() {
        HashMap<String, Double> stringResultDoubleMap = new HashMap<>();
        stringResultDoubleMap.put("@", 10.0d);
        stringResultDoubleMap.put("Key", 10.0d);
        assertEquals(10.0d, LinearRegression.multiplyCoeffsWithValues(stringResultDoubleMap, new HashMap<>()));
    }

    @Test
    void testMultiplyCoeffsWithValues5() {
        HashMap<String, Double> stringResultDoubleMap = new HashMap<>();
        stringResultDoubleMap.put("Key", 10.0d);

        HashMap<String, Double> stringResultDoubleMap1 = new HashMap<>();
        stringResultDoubleMap1.put("Key", 10.0d);
        assertEquals(100.0d, LinearRegression.multiplyCoeffsWithValues(stringResultDoubleMap, stringResultDoubleMap1));
    }
}

