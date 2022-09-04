package microsim.alignment.probability;

import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AbstractSortByDifferenceAlignmentTest {

    @Test
    void testAlign() {
        SBDAlignment<Object> sbdAlignment = new SBDAlignment<>();
        assertThrows(NullPointerException.class,
            () -> sbdAlignment.align(new ArrayList<>(), (Predicate<Object>) mock(Predicate.class), null, 0.25d));
    }

    @Test
    void testSortByComparator() {
        SBDAlignment<Object> sbdAlignment = new SBDAlignment<>();
        assertTrue(sbdAlignment.sortByComparator(new HashMap<>(), true).isEmpty());
    }

    @Test
    void testSortByComparator1() {
        SBDAlignment<Object> sbdAlignment = new SBDAlignment<>();

        HashMap<Object, Double> objectResultDoubleMap = new HashMap<>();
        objectResultDoubleMap.put("Key", 10.0d);
        assertEquals(1, sbdAlignment.sortByComparator(objectResultDoubleMap, true).size());
    }

    @Test
    void testSortByComparator2() {
        SBDAlignment<Object> sbdAlignment = new SBDAlignment<>();

        HashMap<Object, Double> objectResultDoubleMap = new HashMap<>();
        objectResultDoubleMap.put(42, 10.0d);
        objectResultDoubleMap.put("Key", 10.0d);
        assertEquals(2, sbdAlignment.sortByComparator(objectResultDoubleMap, true).size());
    }

    @Test
    void testSortByComparator3() {
        SBDAlignment<Object> sbdAlignment = new SBDAlignment<>();

        HashMap<Object, Double> objectResultDoubleMap = new HashMap<>();
        objectResultDoubleMap.put(1, 10.0d);
        objectResultDoubleMap.put(42, 10.0d);
        objectResultDoubleMap.put("Key", 10.0d);
        assertEquals(3, sbdAlignment.sortByComparator(objectResultDoubleMap, true).size());
    }

    @Test
    void testSortByComparator4() {
        SBDAlignment<Object> sbdAlignment = new SBDAlignment<>();

        HashMap<Object, Double> objectResultDoubleMap = new HashMap<>();
        objectResultDoubleMap.put(42, 10.0d);
        objectResultDoubleMap.put("Key", 10.0d);
        assertEquals(2, sbdAlignment.sortByComparator(objectResultDoubleMap, false).size());
    }
}

