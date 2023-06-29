package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MeanVarianceArrayFunctionTest {

    @Test
    void testConstructor() {
        MeanVarianceArrayFunction actualMeanVarianceArrayFunction = new MeanVarianceArrayFunction(
            mock(DoubleArraySource.class));
        assertTrue(actualMeanVarianceArrayFunction.isCheckingTime());
        assertEquals(0, actualMeanVarianceArrayFunction.type);
        assertTrue(actualMeanVarianceArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor2() {
        MeanVarianceArrayFunction actualMeanVarianceArrayFunction = new MeanVarianceArrayFunction(
            mock(IntArraySource.class));
        assertTrue(actualMeanVarianceArrayFunction.isCheckingTime());
        assertEquals(1, actualMeanVarianceArrayFunction.type);
        assertTrue(actualMeanVarianceArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor3() {
        MeanVarianceArrayFunction actualMeanVarianceArrayFunction = new MeanVarianceArrayFunction(
            mock(LongArraySource.class));
        assertTrue(actualMeanVarianceArrayFunction.isCheckingTime());
        assertEquals(2, actualMeanVarianceArrayFunction.type);
        assertTrue(actualMeanVarianceArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testApply() {
        MeanVarianceArrayFunction meanVarianceArrayFunction = new MeanVarianceArrayFunction(
            mock(DoubleArraySource.class));
        meanVarianceArrayFunction.apply(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(0.0d, meanVarianceArrayFunction.variance);
        assertEquals(10.0d, meanVarianceArrayFunction.mean);
    }

    @Test
    void testApply2() {
        MeanVarianceArrayFunction meanVarianceArrayFunction = new MeanVarianceArrayFunction(
            mock(DoubleArraySource.class));
        meanVarianceArrayFunction.apply(new double[]{0});
        assertEquals(0.0d, meanVarianceArrayFunction.variance);
        assertEquals(0.0d, meanVarianceArrayFunction.mean);
    }

    @Test
    void testApply3() {
        MeanVarianceArrayFunction meanVarianceArrayFunction = new MeanVarianceArrayFunction(
            mock(DoubleArraySource.class));
        meanVarianceArrayFunction.apply(new int[]{1, 1, 1, 1});
        assertEquals(0.0d, meanVarianceArrayFunction.variance);
        assertEquals(1.0d, meanVarianceArrayFunction.mean);
    }

    @Test
    void testApply4() {
        MeanVarianceArrayFunction meanVarianceArrayFunction = new MeanVarianceArrayFunction(
            mock(DoubleArraySource.class));
        meanVarianceArrayFunction.apply(new int[]{0});
        assertEquals(0.0d, meanVarianceArrayFunction.variance);
        assertEquals(0.0d, meanVarianceArrayFunction.mean);
    }

    @Test
    void testApply5() {
        MeanVarianceArrayFunction meanVarianceArrayFunction = new MeanVarianceArrayFunction(
            mock(DoubleArraySource.class));
        meanVarianceArrayFunction.apply(new long[]{1L, 1L, 1L, 1L});
        assertEquals(0.0d, meanVarianceArrayFunction.variance);
        assertEquals(1.0d, meanVarianceArrayFunction.mean);
    }

    @Test
    void testApply6() {
        MeanVarianceArrayFunction meanVarianceArrayFunction = new MeanVarianceArrayFunction(
            mock(DoubleArraySource.class));
        meanVarianceArrayFunction.apply(new long[]{0});
        assertEquals(0.0d, meanVarianceArrayFunction.variance);
        assertEquals(0.0d, meanVarianceArrayFunction.mean);
    }
}

