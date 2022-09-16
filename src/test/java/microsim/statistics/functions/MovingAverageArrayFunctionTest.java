package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MovingAverageArrayFunctionTest {

    @Test
    void testConstructor() {
        MovingAverageArrayFunction actualMovingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(DoubleArraySource.class), 1);
        assertEquals(0.0d, actualMovingAverageArrayFunction.getDoubleValue(mock(Enum.class)));
        assertTrue(actualMovingAverageArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor2() {
        MovingAverageArrayFunction actualMovingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(DoubleArraySource.class), 1);

        assertTrue(actualMovingAverageArrayFunction.isCheckingTime());
        assertEquals(1, actualMovingAverageArrayFunction.window);
        assertEquals(0, actualMovingAverageArrayFunction.type);
        assertTrue(actualMovingAverageArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor3() {
        MovingAverageArrayFunction actualMovingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(IntArraySource.class), 1);

        assertTrue(actualMovingAverageArrayFunction.isCheckingTime());
        assertEquals(1, actualMovingAverageArrayFunction.window);
        assertEquals(1, actualMovingAverageArrayFunction.type);
        assertTrue(actualMovingAverageArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testConstructor4() {
        MovingAverageArrayFunction actualMovingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(LongArraySource.class), 1);

        assertTrue(actualMovingAverageArrayFunction.isCheckingTime());
        assertEquals(1, actualMovingAverageArrayFunction.window);
        assertEquals(2, actualMovingAverageArrayFunction.type);
        assertTrue(actualMovingAverageArrayFunction.timeChecker.isEnabled());
    }

    @Test
    void testApply() {
        MovingAverageArrayFunction movingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(DoubleArraySource.class), 1);
        movingAverageArrayFunction.apply(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(10.0d, movingAverageArrayFunction.mean);
    }

    @Test
    void testApply2() {
        assertThrows(IllegalArgumentException.class, () -> new MovingAverageArrayFunction(
            mock(DoubleArraySource.class), Integer.MIN_VALUE));
    }

    @Test
    void testApply3() {
        MovingAverageArrayFunction movingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(DoubleArraySource.class), 1);
        movingAverageArrayFunction.apply(new double[]{});
        assertEquals(Double.NaN, movingAverageArrayFunction.mean);
    }

    @Test
    void testApply4() {
        MovingAverageArrayFunction movingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(DoubleArraySource.class), 1);
        movingAverageArrayFunction.apply(new int[]{1, 1, 1, 1});
        assertEquals(1.0d, movingAverageArrayFunction.mean);
    }

    @Test
    void testApply5() {
        MovingAverageArrayFunction movingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(DoubleArraySource.class), 1);
        movingAverageArrayFunction.apply(new int[]{});
        assertEquals(Double.NaN, movingAverageArrayFunction.mean);
    }

    @Test
    void testApply6() {
        MovingAverageArrayFunction movingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(DoubleArraySource.class), 1);
        movingAverageArrayFunction.apply(new long[]{1L, 1L, 1L, 1L});
        assertEquals(1.0d, movingAverageArrayFunction.mean);
    }

    @Test
    void testApply9() {
        MovingAverageArrayFunction movingAverageArrayFunction = new MovingAverageArrayFunction(
            mock(DoubleArraySource.class), 1);
        movingAverageArrayFunction.apply(new long[]{});
        assertEquals(Double.NaN, movingAverageArrayFunction.mean);
    }
}

