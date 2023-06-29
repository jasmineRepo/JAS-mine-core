package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class PercentileArrayFunctionTest {

    @Test
    void testConstructor() {
        PercentileArrayFunction actualPercentileArrayFunction = new PercentileArrayFunction(mock(DoubleArraySource.class));
        actualPercentileArrayFunction.apply(new int[]{1, 1, 1, 1});
        actualPercentileArrayFunction.apply(new long[]{1L, 1L, 1L, 1L});
        assertTrue(actualPercentileArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor2() {
        PercentileArrayFunction actualPercentileArrayFunction = new PercentileArrayFunction(
            mock(DoubleArraySource.class));
        assertTrue(actualPercentileArrayFunction.isCheckingTime());
        assertEquals(0, actualPercentileArrayFunction.type);
        assertTrue(actualPercentileArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor3() {
        PercentileArrayFunction actualPercentileArrayFunction = new PercentileArrayFunction(mock(IntArraySource.class));
        assertTrue(actualPercentileArrayFunction.isCheckingTime());
        assertEquals(1, actualPercentileArrayFunction.type);
        assertTrue(actualPercentileArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor4() {
        PercentileArrayFunction actualPercentileArrayFunction = new PercentileArrayFunction(mock(LongArraySource.class));
        assertTrue(actualPercentileArrayFunction.isCheckingTime());
        assertEquals(2, actualPercentileArrayFunction.type);
        assertTrue(actualPercentileArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testApply() {
        PercentileArrayFunction percentileArrayFunction = new PercentileArrayFunction(mock(DoubleArraySource.class));
        percentileArrayFunction.apply(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(10.0d, percentileArrayFunction.p99);
        assertEquals(10.0d, percentileArrayFunction.p90);
        assertEquals(10.0d, percentileArrayFunction.p80);
        assertEquals(10.0d, percentileArrayFunction.p70);
        assertEquals(10.0d, percentileArrayFunction.p60);
        assertEquals(10.0d, percentileArrayFunction.p50);
        assertEquals(10.0d, percentileArrayFunction.p5);
        assertEquals(10.0d, percentileArrayFunction.p40);
        assertEquals(10.0d, percentileArrayFunction.p30);
        assertEquals(10.0d, percentileArrayFunction.p20);
        assertEquals(10.0d, percentileArrayFunction.p10);
        assertEquals(10.0d, percentileArrayFunction.p1);
        assertEquals(10.0d, percentileArrayFunction.p95);
    }

    @Test
    void testApply2() {
        PercentileArrayFunction percentileArrayFunction = new PercentileArrayFunction(mock(DoubleArraySource.class));
        percentileArrayFunction.apply(new double[]{Double.NaN, 10.0d, 10.0d, 10.0d});
        assertEquals(10.0d, percentileArrayFunction.p99);
        assertEquals(10.0d, percentileArrayFunction.p90);
        assertEquals(10.0d, percentileArrayFunction.p80);
        assertEquals(10.0d, percentileArrayFunction.p70);
        assertEquals(10.0d, percentileArrayFunction.p60);
        assertEquals(10.0d, percentileArrayFunction.p50);
        assertEquals(10.0d, percentileArrayFunction.p5);
        assertEquals(10.0d, percentileArrayFunction.p40);
        assertEquals(10.0d, percentileArrayFunction.p30);
        assertEquals(10.0d, percentileArrayFunction.p20);
        assertEquals(10.0d, percentileArrayFunction.p10);
        assertEquals(10.0d, percentileArrayFunction.p1);
        assertEquals(10.0d, percentileArrayFunction.p95);
    }

    @Test
    void testApply3() {
        PercentileArrayFunction percentileArrayFunction = new PercentileArrayFunction(mock(DoubleArraySource.class));
        percentileArrayFunction.apply(new double[]{10.0d, 10.0d, 10.0d, Double.NaN});
        assertEquals(10.0d, percentileArrayFunction.p99);
        assertEquals(10.0d, percentileArrayFunction.p90);
        assertEquals(10.0d, percentileArrayFunction.p80);
        assertEquals(10.0d, percentileArrayFunction.p70);
        assertEquals(10.0d, percentileArrayFunction.p60);
        assertEquals(10.0d, percentileArrayFunction.p50);
        assertEquals(10.0d, percentileArrayFunction.p5);
        assertEquals(10.0d, percentileArrayFunction.p40);
        assertEquals(10.0d, percentileArrayFunction.p30);
        assertEquals(10.0d, percentileArrayFunction.p20);
        assertEquals(10.0d, percentileArrayFunction.p10);
        assertEquals(10.0d, percentileArrayFunction.p1);
        assertEquals(10.0d, percentileArrayFunction.p95);
    }

    @Test
    void testApply4() {
        PercentileArrayFunction percentileArrayFunction = new PercentileArrayFunction(mock(DoubleArraySource.class));
        percentileArrayFunction.apply(new double[]{});
        assertEquals(Double.NaN, percentileArrayFunction.p99);
        assertEquals(Double.NaN, percentileArrayFunction.p90);
        assertEquals(Double.NaN, percentileArrayFunction.p80);
        assertEquals(Double.NaN, percentileArrayFunction.p70);
        assertEquals(Double.NaN, percentileArrayFunction.p60);
        assertEquals(Double.NaN, percentileArrayFunction.p50);
        assertEquals(Double.NaN, percentileArrayFunction.p5);
        assertEquals(Double.NaN, percentileArrayFunction.p40);
        assertEquals(Double.NaN, percentileArrayFunction.p30);
        assertEquals(Double.NaN, percentileArrayFunction.p20);
        assertEquals(Double.NaN, percentileArrayFunction.p10);
        assertEquals(Double.NaN, percentileArrayFunction.p1);
        assertEquals(Double.NaN, percentileArrayFunction.p95);
    }
}

