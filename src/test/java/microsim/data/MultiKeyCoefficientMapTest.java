package microsim.data;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiKeyCoefficientMapTest {
    @Test
    void testConstructor2() {
        assertTrue(
            (new MultiKeyCoefficientMap(new CaseInsensitiveMap(), new String[]{"Keys"}, new String[]{"42"})).isEmpty());
    }

    @Test
    void testConstructor3() {
        assertThrows(NullPointerException.class,
            () -> new MultiKeyCoefficientMap(new CaseInsensitiveMap(), null, new String[]{"42"}));

    }

    @Test
    void testConstructor4() {
        assertTrue((new MultiKeyCoefficientMap(new CaseInsensitiveMap(), new String[]{"Keys"}, null)).isEmpty());
    }

    @Test
    void testConstructor5() {
        assertTrue(
            (new MultiKeyCoefficientMap(new CaseInsensitiveMap(), new String[]{"Keys"}, new String[]{})).isEmpty());
    }

    @Test
    void testConstructor6() {
        assertTrue((new MultiKeyCoefficientMap(new CaseInsensitiveMap(), new String[]{"Keys"}, new String[]{"42", "42"}))
            .isEmpty());
    }

    @Test
    void testConstructor7() {
        assertTrue((new MultiKeyCoefficientMap(new String[]{"Keys"}, new String[]{"42"})).isEmpty());
    }

    @Test
    void testConstructor8() {
        assertThrows(NullPointerException.class, () -> new MultiKeyCoefficientMap(null, null));

    }

    @Test
    void testConstructor9() {
        assertTrue((new MultiKeyCoefficientMap(new String[]{"Keys"}, new String[]{})).isEmpty());
    }

    @Test
    void testConstructor10() {
        assertTrue((new MultiKeyCoefficientMap(new String[]{"Keys"}, new String[]{"42", "42"})).isEmpty());
    }

    @Test
    void testToStringKey() {
        assertEquals("Value", MultiKeyCoefficientMap.toStringKey("Value"));
        assertEquals("10.0", MultiKeyCoefficientMap.toStringKey(10.0d));
        assertEquals("42", MultiKeyCoefficientMap.toStringKey(42));
    }

    @Test
    void testToStringKey2() {
        String actualToStringKeyResult = MultiKeyCoefficientMap.toStringKey(true);
        assertEquals(Boolean.TRUE.toString(), actualToStringKeyResult);
    }
}

