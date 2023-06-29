package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class MinArrayFunctionTest {
    @Test
    void testDoubleApply() {
        MinArrayFunction.Double resultDouble = new MinArrayFunction.Double(mock(DoubleArraySource.class));
        resultDouble.apply(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(10.0d, resultDouble.min);
    }

    @Test
    void testDoubleConstructor() {
        MinArrayFunction.Double actualResultDouble = new MinArrayFunction.Double(mock(DoubleArraySource.class));
        assertEquals(0.0d, actualResultDouble.getDoubleValue(mock(Enum.class)));
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testDoubleConstructor2() {
        MinArrayFunction.Double actualResultDouble = new MinArrayFunction.Double(mock(DoubleArraySource.class));
        assertTrue(actualResultDouble.isCheckingTime());
        assertEquals(0, actualResultDouble.type);
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testIntegerApply() {
        MinArrayFunction.Integer integer = new MinArrayFunction.Integer(mock(IntArraySource.class));
        integer.apply(new int[]{1, 1, 1, 1});
        assertEquals(1, integer.imin);
    }

    @Test
    void testIntegerConstructor() {
        MinArrayFunction.Integer actualInteger = new MinArrayFunction.Integer(mock(IntArraySource.class));
        assertEquals(0, actualInteger.getIntValue(mock(Enum.class)));
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testIntegerConstructor2() {
        MinArrayFunction.Integer actualInteger = new MinArrayFunction.Integer(mock(IntArraySource.class));
        assertTrue(actualInteger.isCheckingTime());
        assertEquals(1, actualInteger.type);
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testIntegerGetDoubleValue() {
        assertEquals(0.0d, (new MinArrayFunction.Integer(mock(IntArraySource.class))).getDoubleValue(mock(Enum.class)));
    }

    @Test
    void testLongApply() {
        MinArrayFunction.Long resultLong = new MinArrayFunction.Long(mock(LongArraySource.class));
        resultLong.apply(new long[]{1L, 1L, 1L, 1L});
        assertEquals(1L, resultLong.lmin);
    }

    @Test
    void testLongConstructor() {
        MinArrayFunction.Long actualResultLong = new MinArrayFunction.Long(mock(LongArraySource.class));
        assertEquals(0L, actualResultLong.getLongValue(mock(Enum.class)));
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testLongConstructor2() {
        MinArrayFunction.Long actualResultLong = new MinArrayFunction.Long(mock(LongArraySource.class));
        assertTrue(actualResultLong.isCheckingTime());
        assertEquals(2, actualResultLong.type);
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testLongGetDoubleValue() {
        assertEquals(0.0d, (new MinArrayFunction.Long(mock(LongArraySource.class))).getDoubleValue(mock(Enum.class)));
    }
}

