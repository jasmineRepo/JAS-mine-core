package microsim.statistics.functions;

import microsim.statistics.DoubleSource;
import microsim.statistics.IntSource;
import microsim.statistics.LongSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaxTraceFunctionTest {

    @Test
    void testApplyFunction() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(10.0d);
        MaxTraceFunction.Double resultDouble = new MaxTraceFunction.Double(doubleSource, mock(Enum.class));
        resultDouble.applyFunction();
        verify(doubleSource).getDoubleValue(any());
        assertEquals(10.0d, resultDouble.getLastRead());
        assertEquals(10.0d, resultDouble.getMax());
        assertEquals(1, resultDouble.count);
    }

    @Test
    void testDoubleApplyFunction() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(Double.MIN_VALUE);
        MaxTraceFunction.Double resultDouble = new MaxTraceFunction.Double(doubleSource, mock(Enum.class));
        resultDouble.applyFunction();
        verify(doubleSource).getDoubleValue(any());
        assertEquals(Double.MIN_VALUE, resultDouble.getLastRead());
        assertEquals(1, resultDouble.count);
    }

    @Test
    void testIntegerApplyFunction() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(42);
        MaxTraceFunction.Integer integer = new MaxTraceFunction.Integer(intSource, mock(Enum.class));
        integer.applyFunction();
        verify(intSource).getIntValue(any());
        assertEquals(42, integer.getLastRead());
        assertEquals(42, integer.getMax());
        assertEquals(1, integer.count);
    }

    @Test
    void testIntegerApplyFunction2() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(Integer.MIN_VALUE);
        MaxTraceFunction.Integer integer = new MaxTraceFunction.Integer(intSource, mock(Enum.class));
        integer.applyFunction();
        verify(intSource).getIntValue(any());
        assertEquals(Integer.MIN_VALUE, integer.getLastRead());
        assertEquals(1, integer.count);
    }

    @Test
    void testLongApplyFunction() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(42L);
        MaxTraceFunction.Long resultLong = new MaxTraceFunction.Long(longSource, mock(Enum.class));
        resultLong.applyFunction();
        verify(longSource).getLongValue(any());
        assertEquals(42L, resultLong.getLastRead());
        assertEquals(42L, resultLong.getMax());
        assertEquals(1, resultLong.count);
    }

    @Test
    void testLongApplyFunction2() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(Long.MIN_VALUE);
        MaxTraceFunction.Long resultLong = new MaxTraceFunction.Long(longSource, mock(Enum.class));
        resultLong.applyFunction();
        verify(longSource).getLongValue(any());
        assertEquals(Long.MIN_VALUE, resultLong.getLastRead());
        assertEquals(1, resultLong.count);
    }

    @Test
    void testDoubleConstructor3() {
        MaxTraceFunction.Double actualResultDouble = new MaxTraceFunction.Double(mock(DoubleSource.class), mock(Enum.class));

        assertEquals(Double.MIN_VALUE, actualResultDouble.getMax());
        assertEquals(0, actualResultDouble.count);
        assertTrue(actualResultDouble.isCheckingTime());
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testIntegerConstructor3() {
        MaxTraceFunction.Integer actualInteger = new MaxTraceFunction.Integer(mock(IntSource.class), mock(Enum.class));

        assertEquals(Integer.MIN_VALUE, actualInteger.getMax());
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
        MaxTraceFunction.Integer actualInteger = new MaxTraceFunction.Integer(integer, mock(Enum.class));

        assertTrue(actualInteger.target instanceof MultiTraceFunction.Integer);
        assertEquals(Integer.MIN_VALUE, actualInteger.getMax());
        assertEquals(0, actualInteger.count);
        assertTrue(actualInteger.isCheckingTime());
        assertTrue(actualInteger.timeChecker.isEnabled());
        verify(intSource).getIntValue(any());
    }

    @Test
    void testLongConstructor3() {
        MaxTraceFunction.Long actualResultLong = new MaxTraceFunction.Long(mock(LongSource.class), mock(Enum.class));

        assertEquals(Long.MIN_VALUE, actualResultLong.getMax());
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
        MaxTraceFunction.Long actualResultLong = new MaxTraceFunction.Long(resultLong, mock(Enum.class));

        assertTrue(actualResultLong.target instanceof MultiTraceFunction.Long);
        assertEquals(Long.MIN_VALUE, actualResultLong.getMax());
        assertEquals(0, actualResultLong.count);
        assertTrue(actualResultLong.isCheckingTime());
        assertTrue(actualResultLong.timeChecker.isEnabled());
        verify(longSource).getLongValue(any());
    }
}

