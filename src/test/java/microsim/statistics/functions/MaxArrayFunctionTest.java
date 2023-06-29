package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MaxArrayFunctionTest {
    @Test
    void testDoubleApply() {
        MaxArrayFunction.Double resultDouble = new MaxArrayFunction.Double(mock(DoubleArraySource.class));
        resultDouble.apply(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(10.0d, resultDouble.dmax);
    }

    @Test
    void testDoubleConstructor() {
        MaxArrayFunction.Double actualResultDouble = new MaxArrayFunction.Double(mock(DoubleArraySource.class));
        assertEquals(0.0d, actualResultDouble.getDoubleValue(mock(Enum.class)));
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testDoubleConstructor2() {
        MaxArrayFunction.Double actualResultDouble = new MaxArrayFunction.Double(mock(DoubleArraySource.class));
        assertTrue(actualResultDouble.isCheckingTime());
        assertEquals(0, actualResultDouble.type);
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testIntegerApply() {
        MaxArrayFunction.Integer integer = new MaxArrayFunction.Integer(mock(IntArraySource.class));
        integer.apply(new int[]{1, 1, 1, 1});
        assertEquals(1, integer.imax);
    }

    @Test
    void testIntegerConstructor() {
        MaxArrayFunction.Integer actualInteger = new MaxArrayFunction.Integer(mock(IntArraySource.class));
        assertEquals(0, actualInteger.getIntValue(mock(Enum.class)));
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testIntegerConstructor2() {
        MaxArrayFunction.Integer actualInteger = new MaxArrayFunction.Integer(mock(IntArraySource.class));
        assertTrue(actualInteger.isCheckingTime());
        assertEquals(1, actualInteger.type);
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testIntegerGetDoubleValue() {
        assertEquals(0.0d, (new MaxArrayFunction.Integer(mock(IntArraySource.class))).getDoubleValue(mock(Enum.class)));
    }

    @Test
    void testLongApply() {
        MaxArrayFunction.Long resultLong = new MaxArrayFunction.Long(mock(LongArraySource.class));
        resultLong.apply(new long[]{1L, 1L, 1L, 1L});
        assertEquals(1L, resultLong.lmax);
    }

    @Test
    void testLongConstructor() {
        MaxArrayFunction.Long actualResultLong = new MaxArrayFunction.Long(mock(LongArraySource.class));
        assertEquals(0L, actualResultLong.getLongValue(mock(Enum.class)));
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testLongConstructor2() {
        MaxArrayFunction.Long actualResultLong = new MaxArrayFunction.Long(mock(LongArraySource.class));
        assertTrue(actualResultLong.isCheckingTime());
        assertEquals(2, actualResultLong.type);
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testLongGetDoubleValue() {
        assertEquals(0.0d, (new MaxArrayFunction.Long(mock(LongArraySource.class))).getDoubleValue(mock(Enum.class)));
    }
}

