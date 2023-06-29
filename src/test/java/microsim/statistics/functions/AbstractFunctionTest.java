package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class AbstractFunctionTest {
    @Test
    void testIsCheckingTime() {
        assertTrue((new CountArrayFunction(mock(DoubleArraySource.class))).isCheckingTime());
    }

    @Test
    void testIsCheckingTime2() {
        CountArrayFunction countArrayFunction = new CountArrayFunction(mock(DoubleArraySource.class));
        countArrayFunction.setCheckingTime(false);
        assertFalse(countArrayFunction.isCheckingTime());
    }

    @Test
    void testSetCheckingTime() {
        CountArrayFunction countArrayFunction = new CountArrayFunction(mock(DoubleArraySource.class));
        countArrayFunction.setCheckingTime(true);
        assertTrue(countArrayFunction.timeChecker.isEnabled());
    }
}

