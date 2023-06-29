package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CountArrayFunctionTest {
    @Test
    void testConstructor() {
        CountArrayFunction actualCountArrayFunction = new CountArrayFunction(mock(DoubleArraySource.class));
        actualCountArrayFunction.apply(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        actualCountArrayFunction.apply(new int[]{1, 1, 1, 1});
        actualCountArrayFunction.apply(new long[]{1L, 1L, 1L, 1L});
        assertEquals(4, actualCountArrayFunction.getIntValue(mock(Enum.class)));
        assertTrue(actualCountArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor2() {
        CountArrayFunction actualCountArrayFunction = new CountArrayFunction(mock(DoubleArraySource.class));
        assertTrue(actualCountArrayFunction.isCheckingTime());
        assertEquals(0, actualCountArrayFunction.type);
        assertTrue(actualCountArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor3() {
        CountArrayFunction actualCountArrayFunction = new CountArrayFunction(mock(IntArraySource.class));
        assertTrue(actualCountArrayFunction.isCheckingTime());
        assertEquals(1, actualCountArrayFunction.type);
        assertTrue(actualCountArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor4() {
        CountArrayFunction actualCountArrayFunction = new CountArrayFunction(mock(LongArraySource.class));
        assertTrue(actualCountArrayFunction.isCheckingTime());
        assertEquals(2, actualCountArrayFunction.type);
        assertTrue(actualCountArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testGetDoubleValue() {
        assertEquals(0.0d, (new CountArrayFunction(mock(DoubleArraySource.class))).getDoubleValue(mock(Enum.class)));
    }
}

