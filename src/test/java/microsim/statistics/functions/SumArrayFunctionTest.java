package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class SumArrayFunctionTest {
    @Test
    void testDoubleApply() {
        SumArrayFunction.Double resultDouble = new SumArrayFunction.Double(mock(DoubleArraySource.class));
        resultDouble.apply(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(40.0d, resultDouble.dsum);
    }

    @Test
    void testDoubleConstructor() {
        SumArrayFunction.Double actualResultDouble = new SumArrayFunction.Double(mock(DoubleArraySource.class));
        assertEquals(0.0d, actualResultDouble.getDoubleValue(null));
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testDoubleConstructor2() {
        SumArrayFunction.Double actualResultDouble = new SumArrayFunction.Double(mock(DoubleArraySource.class));
        assertTrue(actualResultDouble.isCheckingTime());
        assertEquals(0, actualResultDouble.type);
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testIntegerApply() {
        SumArrayFunction.Integer integer = new SumArrayFunction.Integer(mock(IntArraySource.class));
        integer.apply(new int[]{1, 1, 1, 1});
        assertEquals(4, integer.isum);
    }

    @Test
    void testIntegerConstructor() {
        SumArrayFunction.Integer actualInteger = new SumArrayFunction.Integer(mock(IntArraySource.class));
        assertEquals(0, actualInteger.getIntValue(null));
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testIntegerConstructor2() {
        SumArrayFunction.Integer actualInteger = new SumArrayFunction.Integer(mock(IntArraySource.class));
        assertTrue(actualInteger.isCheckingTime());
        assertEquals(1, actualInteger.type);
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testIntegerGetDoubleValue() {
        assertEquals(0.0d, (new SumArrayFunction.Integer(mock(IntArraySource.class))).getDoubleValue(null));
    }

    @Test
    void testLongApply() {
        SumArrayFunction.Long resultLong = new SumArrayFunction.Long(mock(LongArraySource.class));
        resultLong.apply(new long[]{1L, 1L, 1L, 1L});
        assertEquals(4L, resultLong.lsum);
    }

    @Test
    void testLongConstructor() {
        SumArrayFunction.Long actualResultLong = new SumArrayFunction.Long(mock(LongArraySource.class));
        assertEquals(0L, actualResultLong.getLongValue(null));
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testLongConstructor2() {
        SumArrayFunction.Long actualResultLong = new SumArrayFunction.Long(mock(LongArraySource.class));
        assertTrue(actualResultLong.isCheckingTime());
        assertEquals(2, actualResultLong.type);
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testLongGetDoubleValue() {
        assertEquals(0.0d, (new SumArrayFunction.Long(mock(LongArraySource.class))).getDoubleValue(null));
    }
}

