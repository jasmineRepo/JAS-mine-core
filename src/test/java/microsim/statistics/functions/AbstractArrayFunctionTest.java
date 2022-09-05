package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AbstractArrayFunctionTest {
    @Test
    void testApplyFunction() {
        DoubleArraySource doubleArraySource = mock(DoubleArraySource.class);
        when(doubleArraySource.getDoubleArray()).thenReturn(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        CountArrayFunction countArrayFunction = new CountArrayFunction(doubleArraySource);
        countArrayFunction.applyFunction();
        verify(doubleArraySource).getDoubleArray();
        assertEquals(4, countArrayFunction.count);
    }

    @Test
    void testApplyFunction2() {
        LongArraySource longArraySource = mock(LongArraySource.class);
        when(longArraySource.getLongArray()).thenReturn(new long[]{1L, 1L, 1L, 1L});
        CountArrayFunction countArrayFunction = new CountArrayFunction(longArraySource);
        countArrayFunction.applyFunction();
        verify(longArraySource).getLongArray();
        assertEquals(4, countArrayFunction.count);
    }

    @Test
    void testApplyFunction3() {
        IntArraySource intArraySource = mock(IntArraySource.class);
        when(intArraySource.getIntArray()).thenReturn(new int[]{1, 1, 1, 1});
        CountArrayFunction countArrayFunction = new CountArrayFunction(intArraySource);
        countArrayFunction.applyFunction();
        verify(intArraySource).getIntArray();
        assertEquals(4, countArrayFunction.count);
    }

    @Test
    void testApplyFunction4() {
        IntArraySource intArraySource = mock(IntArraySource.class);
        when(intArraySource.getIntArray()).thenThrow(new UnsupportedOperationException("An error occurred"));
        assertThrows(UnsupportedOperationException.class, () -> (new CountArrayFunction(intArraySource)).applyFunction());
        verify(intArraySource).getIntArray();
    }

    @Test
    void testApply() {
        CountArrayFunction countArrayFunction = new CountArrayFunction(mock(DoubleArraySource.class));
        countArrayFunction.apply(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(4, countArrayFunction.count);
    }

    @Test
    void testApply2() {
        CountArrayFunction countArrayFunction = new CountArrayFunction(mock(DoubleArraySource.class));
        countArrayFunction.apply(new int[]{1, 1, 1, 1});
        assertEquals(4, countArrayFunction.count);
    }

    @Test
    void testApply3() {
        CountArrayFunction countArrayFunction = new CountArrayFunction(mock(DoubleArraySource.class));
        countArrayFunction.apply(new long[]{1L, 1L, 1L, 1L});
        assertEquals(4, countArrayFunction.count);
    }
}

