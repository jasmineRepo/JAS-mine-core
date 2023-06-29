package microsim.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiKeyHashMapTest {
    @Test
    void testGetHashKey() {
        assertEquals(-1491480296, (new MultiKeyHashMap()).getHashKey(new Object[]{"Key Array"}));
    }

    @Test
    void testContainsKey() {
        assertFalse((new MultiKeyHashMap()).containsKey("Key Array"));
    }

    @Test
    void testContainsKey2() {
        MultiKeyHashMap multiKeyHashMap = new MultiKeyHashMap();
        multiKeyHashMap.put(1, new MultiKeyHashMap.EntryValue(new Object[]{"Key Array"}, new Object[]{"Value Array"}));
        assertTrue(multiKeyHashMap.containsKey(1));
    }

    @Test
    void testPut3() {
        MultiKeyHashMap multiKeyHashMap = new MultiKeyHashMap();
        multiKeyHashMap.put(1, new MultiKeyHashMap.EntryValue(new Object[]{"Key Array"}, new Object[]{"Value Array"}));
        Object[] objectArray = new Object[]{1};
        Object[] objectArray1 = new Object[]{"Value Array"};
        assertEquals(1, multiKeyHashMap.put(objectArray, objectArray1).length);
        MultiKeyHashMap.EntryValue getResult = multiKeyHashMap.get(1);
        assertSame(objectArray, getResult.getKeyArray());
        assertSame(objectArray1, getResult.getValueArray());
    }

    @Test
    void testRemove3() {
        MultiKeyHashMap multiKeyHashMap = new MultiKeyHashMap();
        multiKeyHashMap.put(1, new MultiKeyHashMap.EntryValue(new Object[]{"Key Array"}, new Object[]{"Value Array"}));
        multiKeyHashMap.put(2, new MultiKeyHashMap.EntryValue(new Object[]{"Key Array"}, new Object[]{"Value Array"}));

        assertEquals("Value Array", multiKeyHashMap.remove(1).getValueArray()[0]);
    }

    @Test
    void testGet3() {
        MultiKeyHashMap multiKeyHashMap = new MultiKeyHashMap();
        multiKeyHashMap.put(1, new MultiKeyHashMap.EntryValue(new Object[]{"Key Array"}, new Object[]{"Value Array"}));
        assertEquals("Value Array", multiKeyHashMap.get(1).getValueArray()[0]);
    }
}

