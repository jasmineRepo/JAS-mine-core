package microsim.statistics.functions;

import microsim.statistics.DoubleSource;
import microsim.statistics.IntSource;
import microsim.statistics.LongSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MinTraceFunctionTest {

    @Test
    void testApplyFunction() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(10.0d);
        MinTraceFunction.Double resultDouble = new MinTraceFunction.Double(doubleSource, mock(Enum.class));
        resultDouble.applyFunction();
        verify(doubleSource).getDoubleValue(any());
        assertEquals(10.0d, resultDouble.getLastRead());
        assertEquals(10.0d, resultDouble.getMin());
        assertEquals(1, resultDouble.count);
    }

    @Test
    void testDoubleApplyFunction() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(Double.MAX_VALUE);
        MinTraceFunction.Double resultDouble = new MinTraceFunction.Double(doubleSource, mock(Enum.class));
        resultDouble.applyFunction();
        verify(doubleSource).getDoubleValue(any());
        assertEquals(Double.MAX_VALUE, resultDouble.getLastRead());
        assertEquals(1, resultDouble.count);
    }

    @Test
    void testIntegerApplyFunction() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(42);
        MinTraceFunction.Integer integer = new MinTraceFunction.Integer(intSource, mock(Enum.class));
        integer.applyFunction();
        verify(intSource).getIntValue(any());
        assertEquals(42, integer.getLastRead());
        assertEquals(42, integer.getMin());
        assertEquals(1, integer.count);
    }

    @Test
    void testIntegerApplyFunction2() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(Integer.MAX_VALUE);
        MinTraceFunction.Integer integer = new MinTraceFunction.Integer(intSource, mock(Enum.class));
        integer.applyFunction();
        verify(intSource).getIntValue(any());
        assertEquals(Integer.MAX_VALUE, integer.getLastRead());
        assertEquals(1, integer.count);
    }

    @Test
    void testLongApplyFunction() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(42L);
        MinTraceFunction.Long resultLong = new MinTraceFunction.Long(longSource, mock(Enum.class));
        resultLong.applyFunction();
        verify(longSource).getLongValue(any());
        assertEquals(42L, resultLong.getLastRead());
        assertEquals(42L, resultLong.getMin());
        assertEquals(1, resultLong.count);
    }

    @Test
    void testLongApplyFunction2() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(Long.MAX_VALUE);
        MinTraceFunction.Long resultLong = new MinTraceFunction.Long(longSource, mock(Enum.class));
        resultLong.applyFunction();
        verify(longSource).getLongValue(any());
        assertEquals(Long.MAX_VALUE, resultLong.getLastRead());
        assertEquals(1, resultLong.count);
    }

    @Test
    void testDoubleConstructor3() {
        MinTraceFunction.Double actualResultDouble = new MinTraceFunction.Double(mock(DoubleSource.class), mock(Enum.class));

        assertEquals(Double.MAX_VALUE, actualResultDouble.getMin());
        assertEquals(0, actualResultDouble.count);
        assertTrue(actualResultDouble.isCheckingTime());
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testIntegerConstructor3() {
        MinTraceFunction.Integer actualInteger = new MinTraceFunction.Integer(mock(IntSource.class), mock(Enum.class));

        assertEquals(Integer.MAX_VALUE, actualInteger.getMin());
        assertEquals(0, actualInteger.count);
        assertTrue(actualInteger.isCheckingTime());
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testIntegerConstructor4() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(42);

        MultiTraceFunction.Integer integer = new MultiTraceFunction.Integer(intSource, mock(Enum.class));
        integer.updateSource();
        MinTraceFunction.Integer actualInteger = new MinTraceFunction.Integer(integer, mock(Enum.class));

        assertTrue(actualInteger.target instanceof MultiTraceFunction.Integer);
        assertEquals(Integer.MAX_VALUE, actualInteger.getMin());
        assertEquals(0, actualInteger.count);
        assertTrue(actualInteger.isCheckingTime());
        assertTrue(actualInteger.timeChecker.isEnabled());
        verify(intSource).getIntValue(any());
    }

    @Test
    void testLongConstructor3() {
        MinTraceFunction.Long actualResultLong = new MinTraceFunction.Long(mock(LongSource.class), mock(Enum.class));

        assertEquals(Long.MAX_VALUE, actualResultLong.getMin());
        assertEquals(0, actualResultLong.count);
        assertTrue(actualResultLong.isCheckingTime());
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testLongConstructor4() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(42L);

        MultiTraceFunction.Long resultLong = new MultiTraceFunction.Long(longSource, mock(Enum.class));
        resultLong.updateSource();
        MinTraceFunction.Long actualResultLong = new MinTraceFunction.Long(resultLong, mock(Enum.class));

        assertTrue(actualResultLong.target instanceof MultiTraceFunction.Long);
        assertEquals(Long.MAX_VALUE, actualResultLong.getMin());
        assertEquals(0, actualResultLong.count);
        assertTrue(actualResultLong.isCheckingTime());
        assertTrue(actualResultLong.timeChecker.isEnabled());
        verify(longSource).getLongValue(any());
    }
}

