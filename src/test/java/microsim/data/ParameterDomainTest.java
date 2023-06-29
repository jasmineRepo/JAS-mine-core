package microsim.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParameterDomainTest {
    @Test
    void testConstructor() {
        ParameterDomain actualParameterDomain = new ParameterDomain();
        assertNull(actualParameterDomain.getName());
        assertNull(actualParameterDomain.getValues());
    }

    @Test
    void testConstructor2() {
        ParameterDomain actualParameterDomain = new ParameterDomain("Name", new Object[]{"Values"});

        assertEquals("Name", actualParameterDomain.getName());
        assertEquals(1, actualParameterDomain.getValues().length);
    }

    @Test
    void testAddValue() {
        ParameterDomain parameterDomain = new ParameterDomain();
        ParameterDomain actualAddValueResult = parameterDomain.addValue("Value");
        assertSame(parameterDomain, actualAddValueResult);
        assertEquals(1, actualAddValueResult.getValues().length);
    }

    @Test
    void testAddValue2() {
        ParameterDomain parameterDomain = new ParameterDomain("Name", new Object[]{"Values"});
        ParameterDomain actualAddValueResult = parameterDomain.addValue("Value");
        assertSame(parameterDomain, actualAddValueResult);
        assertEquals(2, actualAddValueResult.getValues().length);
    }
}

