package microsim.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParameterRangeDomainTest {
    @Test
    void testConstructor() {
        ParameterRangeDomain actualParameterRangeDomain = new ParameterRangeDomain();
        assertNull(actualParameterRangeDomain.getMax());
        assertNull(actualParameterRangeDomain.getStep());
        assertNull(actualParameterRangeDomain.getName());
        assertNull(actualParameterRangeDomain.getMin());
    }

    @Test
    void testConstructor2() {
        ParameterRangeDomain actualParameterRangeDomain = new ParameterRangeDomain("Name", 10.0d, 10.0d, 10.0d);

        assertEquals(10.0d, actualParameterRangeDomain.getMax().doubleValue());
        assertEquals(10.0d, actualParameterRangeDomain.getStep().doubleValue());
        assertEquals("Name", actualParameterRangeDomain.getName());
        assertEquals(10.0d, actualParameterRangeDomain.getMin().doubleValue());
    }

    @Test
    void testGetValues2() {
        assertEquals(0, (new ParameterRangeDomain("Name", 10.0d, 10.0d, 10.0d)).getValues().length);
    }

    @Test
    void testGetValues4() {
        assertEquals(1, (new ParameterRangeDomain("Name", 0.5d, 10.0d, 10.0d)).getValues().length);
    }

    @Test
    void testSetValues() {
        assertThrows(UnsupportedOperationException.class,
            () -> (new ParameterRangeDomain()).setValues(new Object[]{"Values"}));
    }
}

