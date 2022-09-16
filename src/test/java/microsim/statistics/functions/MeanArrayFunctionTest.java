package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MeanArrayFunctionTest {
    @Test
    void testConstructor() {
        MeanArrayFunction actualMeanArrayFunction = new MeanArrayFunction(mock(DoubleArraySource.class));
        assertEquals(0.0d, actualMeanArrayFunction.getDoubleValue(mock(Enum.class)));
        assertTrue(actualMeanArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor2() {
        MeanArrayFunction actualMeanArrayFunction = new MeanArrayFunction(mock(DoubleArraySource.class));
        assertTrue(actualMeanArrayFunction.isCheckingTime());
        assertEquals(0, actualMeanArrayFunction.type);
        assertTrue(actualMeanArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor3() {
        MeanArrayFunction actualMeanArrayFunction = new MeanArrayFunction(mock(IntArraySource.class));
        assertTrue(actualMeanArrayFunction.isCheckingTime());
        assertEquals(1, actualMeanArrayFunction.type);
        assertTrue(actualMeanArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor4() {
        MeanArrayFunction actualMeanArrayFunction = new MeanArrayFunction(mock(LongArraySource.class));
        assertTrue(actualMeanArrayFunction.isCheckingTime());
        assertEquals(2, actualMeanArrayFunction.type);
        assertTrue(actualMeanArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testApply() {
        MeanArrayFunction meanArrayFunction = new MeanArrayFunction(mock(DoubleArraySource.class));
        meanArrayFunction.apply(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(10.0d, meanArrayFunction.mean);
    }

    @Test
    void testApply2() {
        MeanArrayFunction meanArrayFunction = new MeanArrayFunction(mock(DoubleArraySource.class));
        meanArrayFunction.apply(new double[]{0});
        assertEquals(0.0d, meanArrayFunction.mean);
        assertThrows(IllegalArgumentException.class, () -> meanArrayFunction.apply(new double[]{}));
    }

    @Test
    void testApply3() {
        MeanArrayFunction meanArrayFunction = new MeanArrayFunction(mock(DoubleArraySource.class));
        meanArrayFunction.apply(new int[]{1, 1, 1, 1});
        assertEquals(1.0d, meanArrayFunction.mean);
    }

    @Test
    void testApply4() {
        MeanArrayFunction meanArrayFunction = new MeanArrayFunction(mock(DoubleArraySource.class));
        meanArrayFunction.apply(new int[]{});
        assertEquals(0.0d, meanArrayFunction.mean);
    }

    @Test
    void testApply5() {
        MeanArrayFunction meanArrayFunction = new MeanArrayFunction(mock(DoubleArraySource.class));
        meanArrayFunction.apply(new long[]{1L, 1L, 1L, 1L});
        assertEquals(1.0d, meanArrayFunction.mean);
    }

    @Test
    void testApply6() {
        MeanArrayFunction meanArrayFunction = new MeanArrayFunction(mock(DoubleArraySource.class));
        meanArrayFunction.apply(new long[]{});
        assertEquals(0.0d, meanArrayFunction.mean);
    }
}

