package microsim.statistics;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CrossSectionTest {
    @Test
    void testDoubleConstructor() {
        CrossSection.Double actualResultDouble = new CrossSection.Double(new ArrayList<>());
        assertTrue(actualResultDouble.target.isEmpty());
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testDoubleConstructor2() {
        CrossSection.Double actualResultDouble = new CrossSection.Double(new ArrayList<>());
        assertTrue(actualResultDouble.valueID instanceof DoubleSource.Variables);
        assertNull(actualResultDouble.getFilter());
        assertTrue(actualResultDouble.target.isEmpty());
        assertTrue(actualResultDouble.isCheckingTime());
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testDoubleConstructor6() {
        CrossSection.Double actualResultDouble = new CrossSection.Double(new ArrayList<>(), mock(Enum.class));
        assertInstanceOf(Enum.class, actualResultDouble.valueID);
        assertTrue(actualResultDouble.target.isEmpty());
        assertNull(actualResultDouble.getFilter());
        assertTrue(actualResultDouble.isCheckingTime());
        assertTrue(actualResultDouble.timeChecker.isEnabled());
    }

    @Test
    void testIntegerConstructor() {
        CrossSection.Integer actualInteger = new CrossSection.Integer(new ArrayList<>());
        assertTrue(actualInteger.target.isEmpty());
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testIntegerConstructor2() {
        CrossSection.Integer actualInteger = new CrossSection.Integer(new ArrayList<>());
        assertNull(actualInteger.getFilter());
        assertTrue(actualInteger.valueID instanceof IntSource.Variables);
        assertTrue(actualInteger.target.isEmpty());
        assertTrue(actualInteger.isCheckingTime());
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testIntegerConstructor6() {
        CrossSection.Integer actualInteger = new CrossSection.Integer(new ArrayList<>(), mock(Enum.class));

        assertNull(actualInteger.getFilter());
        assertInstanceOf(Enum.class, actualInteger.valueID);
        assertTrue(actualInteger.target.isEmpty());
        assertTrue(actualInteger.isCheckingTime());
        assertTrue(actualInteger.timeChecker.isEnabled());
    }

    @Test
    void testLongConstructor() {
        CrossSection.Long actualResultLong = new CrossSection.Long(new ArrayList<>());
        assertTrue(actualResultLong.target.isEmpty());
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testLongConstructor2() {
        CrossSection.Long actualResultLong = new CrossSection.Long(new ArrayList<>());
        assertNull(actualResultLong.getFilter());
        assertTrue(actualResultLong.valueID instanceof LongSource.Variables);
        assertTrue(actualResultLong.target.isEmpty());
        assertTrue(actualResultLong.isCheckingTime());
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testLongConstructor6() {
        assertThrows(NullPointerException.class, () -> new CrossSection.Long(new ArrayList<>(), null));
        CrossSection.Long actualResultLong = new CrossSection.Long(new ArrayList<>(), mock(Enum.class));

        assertNull(actualResultLong.getFilter());
        assertInstanceOf(Enum.class, actualResultLong.valueID);
        assertTrue(actualResultLong.target.isEmpty());
        assertTrue(actualResultLong.isCheckingTime());
        assertTrue(actualResultLong.timeChecker.isEnabled());
    }

    @Test
    void testGetSourceArray() {
        assertNull((new CrossSection.Double(new ArrayList<>())).getSourceArray());
    }

    @Test
    void testIsCheckingTime() {
        assertTrue((new CrossSection.Double(new ArrayList<>())).isCheckingTime());
    }

    @Test
    void testIsCheckingTime2() {
        CrossSection.Double resultDouble = new CrossSection.Double(new ArrayList<>());
        resultDouble.setCheckingTime(false);
        assertFalse(resultDouble.isCheckingTime());
    }

    @Test
    void testSetCheckingTime() {
        CrossSection.Double resultDouble = new CrossSection.Double(new ArrayList<>());
        resultDouble.setCheckingTime(true);
        assertTrue(resultDouble.timeChecker.isEnabled());
    }
}

