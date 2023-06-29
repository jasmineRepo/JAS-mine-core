package microsim.data;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MultiKeyCoefficientMapFactoryTest {
    @Test
    void testCreateMapFromAnnotatedList()
        throws IllegalArgumentException, SecurityException {
        assertThrows(IllegalArgumentException.class,
            () -> MultiKeyCoefficientMapFactory.createMapFromAnnotatedList(new ArrayList<>()));
        assertThrows(IllegalArgumentException.class,
            () -> MultiKeyCoefficientMapFactory.createMapFromAnnotatedList(null));
    }

    @Test
    void testCreateMapFromAnnotatedList2()
        throws IllegalArgumentException, SecurityException {
        ArrayList<Object> objectList = new ArrayList<>();
        objectList.add("42");
        assertThrows(IllegalArgumentException.class,
            () -> MultiKeyCoefficientMapFactory.createMapFromAnnotatedList(objectList));
    }
}

