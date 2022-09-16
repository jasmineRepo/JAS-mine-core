package microsim.statistics.functions;

import microsim.statistics.DoubleSource;
import microsim.statistics.IntSource;
import microsim.statistics.LongSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovingAverageTraceFunctionTest {

    @Test
    void testConstructor() {
        MovingAverageTraceFunction actualMovingAverageTraceFunction = new MovingAverageTraceFunction(mock(IntSource.class),
            mock(Enum.class), 3);
        assertEquals(0.0d, actualMovingAverageTraceFunction.getDoubleValue(mock(Enum.class)));
        assertTrue(actualMovingAverageTraceFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor2() {
        MovingAverageTraceFunction actualMovingAverageTraceFunction = new MovingAverageTraceFunction(
            mock(DoubleSource.class), mock(Enum.class), 3);

        assertTrue(actualMovingAverageTraceFunction.isCheckingTime());
        assertEquals(3, actualMovingAverageTraceFunction.values.length);
        assertInstanceOf(Enum.class, actualMovingAverageTraceFunction.valueID);
        assertEquals(0, actualMovingAverageTraceFunction.valueCount);
        assertEquals(3, actualMovingAverageTraceFunction.len);
        assertTrue(actualMovingAverageTraceFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor3() {
        assertThrows(IllegalArgumentException.class,
            () -> new MovingAverageTraceFunction(mock(DoubleSource.class), mock(Enum.class), -1));

    }

    @Test
    void testConstructor4() {
        MovingAverageTraceFunction actualMovingAverageTraceFunction = new MovingAverageTraceFunction(
            mock(IntSource.class), mock(Enum.class), 3);

        assertTrue(actualMovingAverageTraceFunction.isCheckingTime());
        assertEquals(3, actualMovingAverageTraceFunction.values.length);
        assertInstanceOf(Enum.class, actualMovingAverageTraceFunction.valueID);
        assertEquals(0, actualMovingAverageTraceFunction.valueCount);
        assertEquals(3, actualMovingAverageTraceFunction.len);
        assertTrue(actualMovingAverageTraceFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor5() {
        assertThrows(IllegalArgumentException.class,
            () -> new MovingAverageTraceFunction(mock(IntSource.class), mock(Enum.class), -1));

    }

    @Test
    void testConstructor6() {
        MovingAverageTraceFunction actualMovingAverageTraceFunction = new MovingAverageTraceFunction(
            mock(LongSource.class), mock(Enum.class), 3);

        assertTrue(actualMovingAverageTraceFunction.isCheckingTime());
        assertEquals(3, actualMovingAverageTraceFunction.values.length);
        assertInstanceOf(Enum.class, actualMovingAverageTraceFunction.valueID);
        assertEquals(0, actualMovingAverageTraceFunction.valueCount);
        assertEquals(3, actualMovingAverageTraceFunction.len);
        assertTrue(actualMovingAverageTraceFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor7() {
        assertThrows(IllegalArgumentException.class,
            () -> new MovingAverageTraceFunction(mock(LongSource.class), mock(Enum.class), -1));

    }

    @Test
    void testApplyFunction2() {
        DoubleSource doubleSource = mock(DoubleSource.class);
        when(doubleSource.getDoubleValue(any())).thenReturn(10.0d);
        MovingAverageTraceFunction movingAverageTraceFunction = new MovingAverageTraceFunction(doubleSource, mock(Enum.class), 3);
        movingAverageTraceFunction.applyFunction();
        verify(doubleSource).getDoubleValue(any());
        assertEquals(1, movingAverageTraceFunction.valueCount);
        assertEquals(10.0d, movingAverageTraceFunction.average);
    }

}

