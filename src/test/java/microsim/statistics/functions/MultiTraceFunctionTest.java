package microsim.statistics.functions;

import microsim.statistics.DoubleSource;
import microsim.statistics.IntSource;
import microsim.statistics.LongSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MultiTraceFunctionTest {
    @Test
    void testDoubleConstructor() {
        MultiTraceFunction.Double actualResultDouble = new MultiTraceFunction.Double(mock(DoubleSource.class), null);

        assertEquals(0, actualResultDouble.getCount());
        assertEquals(0.0d, actualResultDouble.getVariance());
        assertEquals(0.0d, actualResultDouble.getSumSquare());
        assertEquals(0.0d, actualResultDouble.getSum());
        assertEquals(Double.MAX_VALUE, actualResultDouble.getMin());
        assertEquals(0.0d, actualResultDouble.getMean());
        assertEquals(Double.MIN_VALUE, actualResultDouble.getMax());
        assertEquals(0.0d, actualResultDouble.getLastRead());
    }

    @Test
    void testDoubleUpdateSource() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(Double.MAX_VALUE);
        MultiTraceFunction.Double resultDouble = new MultiTraceFunction.Double(doubleSource, null);
        resultDouble.updateSource();
        verify(doubleSource).getDoubleValue(any());
        assertEquals(1, resultDouble.getCount());
        assertEquals(Double.POSITIVE_INFINITY, resultDouble.getSumSquare());
        assertEquals(Double.MAX_VALUE, resultDouble.getSum());
        assertEquals(Double.MAX_VALUE, resultDouble.getMax());
        assertEquals(Double.MAX_VALUE, resultDouble.getLastRead());
    }

    @Test
    void testDoubleUpdateSource2() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(Double.MIN_VALUE);
        MultiTraceFunction.Double resultDouble = new MultiTraceFunction.Double(doubleSource, null);
        resultDouble.updateSource();
        verify(doubleSource).getDoubleValue(any());
        assertEquals(1, resultDouble.getCount());
        assertEquals(0.0d, resultDouble.getSumSquare());
        assertEquals(Double.MIN_VALUE, resultDouble.getSum());
        assertEquals(Double.MIN_VALUE, resultDouble.getMin());
        assertEquals(Double.MIN_VALUE, resultDouble.getLastRead());
    }

    @Test
    void testIntegerUpdateSource() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(42);
        MultiTraceFunction.Integer integer = new MultiTraceFunction.Integer(intSource, null);
        integer.updateSource();
        verify(intSource).getIntValue(any());
        assertEquals(1, integer.getCount());
        assertEquals(1764L, integer.sumSquare);
        assertEquals(42, integer.getSum());
        assertEquals(42, integer.getMin());
        assertEquals(42, integer.getMax());
        assertEquals(42, integer.getLastRead());
    }

    @Test
    void testIntegerUpdateSource2() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(Integer.MAX_VALUE);
        MultiTraceFunction.Integer integer = new MultiTraceFunction.Integer(intSource, null);
        integer.updateSource();
        verify(intSource).getIntValue(any());
        assertEquals(1, integer.getCount());
        assertEquals(1L, integer.sumSquare);
        assertEquals(Integer.MAX_VALUE, integer.getSum());
        assertEquals(Integer.MAX_VALUE, integer.getMax());
        assertEquals(Integer.MAX_VALUE, integer.getLastRead());
    }

    @Test
    void testIntegerUpdateSource3() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(Integer.MIN_VALUE);
        MultiTraceFunction.Integer integer = new MultiTraceFunction.Integer(intSource, null);
        integer.updateSource();
        verify(intSource).getIntValue(any());
        assertEquals(1, integer.getCount());
        assertEquals(0L, integer.sumSquare);
        assertEquals(Integer.MIN_VALUE, integer.getSum());
        assertEquals(Integer.MIN_VALUE, integer.getMin());
        assertEquals(Integer.MIN_VALUE, integer.getLastRead());
    }

    @Test
    void testIntegerUpdateSource4() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any()))
            .thenThrow(new UnsupportedOperationException("An error occurred"));
        assertThrows(UnsupportedOperationException.class,
            () -> (new MultiTraceFunction.Integer(intSource, null)).updateSource());
        verify(intSource).getIntValue(any());
    }

    @Test
    void testLongUpdateSource() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(42L);
        MultiTraceFunction.Long resultLong = new MultiTraceFunction.Long(longSource, null);
        resultLong.updateSource();
        verify(longSource).getLongValue(any());
        assertEquals(1, resultLong.getCount());
        assertEquals(1764L, resultLong.getSumSquare());
        assertEquals(42L, resultLong.getSum());
        assertEquals(42L, resultLong.getMin());
        assertEquals(42L, resultLong.getMax());
        assertEquals(42L, resultLong.getLastRead());
    }

    @Test
    void testLongUpdateSource2() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(Long.MAX_VALUE);
        MultiTraceFunction.Long resultLong = new MultiTraceFunction.Long(longSource, null);
        resultLong.updateSource();
        verify(longSource).getLongValue(any());
        assertEquals(1, resultLong.getCount());
        assertEquals(1L, resultLong.getSumSquare());
        assertEquals(Long.MAX_VALUE, resultLong.getSum());
        assertEquals(Long.MAX_VALUE, resultLong.getMax());
        assertEquals(Long.MAX_VALUE, resultLong.getLastRead());
    }

    @Test
    void testLongUpdateSource3() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(Long.MIN_VALUE);
        MultiTraceFunction.Long resultLong = new MultiTraceFunction.Long(longSource, null);
        resultLong.updateSource();
        verify(longSource).getLongValue(any());
        assertEquals(1, resultLong.getCount());
        assertEquals(0L, resultLong.getSumSquare());
        assertEquals(Long.MIN_VALUE, resultLong.getSum());
        assertEquals(Long.MIN_VALUE, resultLong.getMin());
        assertEquals(Long.MIN_VALUE, resultLong.getLastRead());
    }

    @Test
    void testLongUpdateSource4() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any()))
            .thenThrow(new UnsupportedOperationException("An error occurred"));
        assertThrows(UnsupportedOperationException.class,
            () -> (new MultiTraceFunction.Long(longSource, null)).updateSource());
        verify(longSource).getLongValue(any());
    }

    @Test
    void testIntegerConstructor() {
        MultiTraceFunction.Integer actualInteger = new MultiTraceFunction.Integer(mock(IntSource.class), null);

        assertEquals(0, actualInteger.getCount());
        assertEquals(0L, actualInteger.sumSquare);
        assertEquals(0.0d, actualInteger.getVariance());
        assertEquals(0, actualInteger.getSum());
        assertEquals(Integer.MAX_VALUE, actualInteger.getMin());
        assertEquals(0.0d, actualInteger.getMean());
        assertEquals(Integer.MIN_VALUE, actualInteger.getMax());
        assertEquals(0, actualInteger.getLastRead());
    }

    @Test
    void testLongConstructor() {
        MultiTraceFunction.Long actualResultLong = new MultiTraceFunction.Long(mock(LongSource.class), null);

        assertEquals(0, actualResultLong.getCount());
        assertEquals(0.0d, actualResultLong.getVariance());
        assertEquals(0L, actualResultLong.getSumSquare());
        assertEquals(0L, actualResultLong.getSum());
        assertEquals(Long.MAX_VALUE, actualResultLong.getMin());
        assertEquals(0.0d, actualResultLong.getMean());
        assertEquals(Long.MIN_VALUE, actualResultLong.getMax());
        assertEquals(0L, actualResultLong.getLastRead());
    }

    @Test
    void testUpdateSource() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(10.0d);
        MultiTraceFunction.Double resultDouble = new MultiTraceFunction.Double(doubleSource, null);
        resultDouble.updateSource();
        verify(doubleSource).getDoubleValue(any());
        assertEquals(1, resultDouble.getCount());
        assertEquals(100.0d, resultDouble.getSumSquare());
        assertEquals(10.0d, resultDouble.getSum());
        assertEquals(10.0d, resultDouble.getMin());
        assertEquals(10.0d, resultDouble.getMax());
        assertEquals(10.0d, resultDouble.getLastRead());
    }

    @Test
    void testDoubleGetMean() {
        assertEquals(0.0d, (new MultiTraceFunction.Double(mock(DoubleSource.class), null)).getMean());
    }

    @Test
    void testDoubleGetMean2() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(10.0d);

        MultiTraceFunction.Double resultDouble = new MultiTraceFunction.Double(doubleSource, null);
        resultDouble.updateSource();
        assertEquals(10.0d, resultDouble.getMean());
        verify(doubleSource).getDoubleValue(any());
    }

    @Test
    void testIntegerGetMean() {
        assertEquals(0.0d, (new MultiTraceFunction.Integer(mock(IntSource.class), null)).getMean());
    }

    @Test
    void testIntegerGetMean2() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(42);

        MultiTraceFunction.Integer integer = new MultiTraceFunction.Integer(intSource, null);
        integer.updateSource();
        assertEquals(42.0d, integer.getMean());
        verify(intSource).getIntValue(any());
    }

    @Test
    void testLongGetMean() {
        assertEquals(0.0d, (new MultiTraceFunction.Long(mock(LongSource.class), null)).getMean());
    }

    @Test
    void testLongGetMean2() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(42L);

        MultiTraceFunction.Long resultLong = new MultiTraceFunction.Long(longSource, null);
        resultLong.updateSource();
        assertEquals(42.0d, resultLong.getMean());
        verify(longSource).getLongValue(any());
    }

    @Test
    void testDoubleGetVariance() {
        assertEquals(0.0d, (new MultiTraceFunction.Double(mock(DoubleSource.class), null)).getVariance());
    }

    @Test
    void testDoubleGetVariance2() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(10.0d);

        MultiTraceFunction.Double resultDouble = new MultiTraceFunction.Double(doubleSource, null);
        resultDouble.updateSource();
        assertEquals(0.0d, resultDouble.getVariance());
        verify(doubleSource).getDoubleValue(any());
    }

    @Test
    void testIntegerGetVariance() {
        assertEquals(0.0d, (new MultiTraceFunction.Integer(mock(IntSource.class), null)).getVariance());
    }

    @Test
    void testIntegerGetVariance2() {
        IntSource intSource = mock(IntSource.class);
        when(intSource.getIntValue(any())).thenReturn(42);

        MultiTraceFunction.Integer integer = new MultiTraceFunction.Integer(intSource, null);
        integer.updateSource();
        assertEquals(0.0d, integer.getVariance());
        verify(intSource).getIntValue(any());
    }

    @Test
    void testLongGetVariance() {
        assertEquals(0.0d, (new MultiTraceFunction.Long(mock(LongSource.class), null)).getVariance());
    }

    @Test
    void testLongGetVariance2() {
        LongSource longSource = mock(LongSource.class);
        when(longSource.getLongValue(any())).thenReturn(42L);

        MultiTraceFunction.Long resultLong = new MultiTraceFunction.Long(longSource, null);
        resultLong.updateSource();
        assertEquals(0.0d, resultLong.getVariance());
        verify(longSource).getLongValue(any());
    }

    @Test
    void testGetCount() {
        assertEquals(0, (new MultiTraceFunction.Double(mock(DoubleSource.class), null)).getCount());
    }

    @Test
    void testGetCount2() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(10.0d);

        MultiTraceFunction.Double resultDouble = new MultiTraceFunction.Double(doubleSource, null);
        resultDouble.updateSource();
        assertEquals(1, resultDouble.getCount());
        verify(doubleSource).getDoubleValue(any());
    }
}

