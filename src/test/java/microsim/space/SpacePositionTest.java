package microsim.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpacePositionTest {

    @Test
    void testConstructor() {
        SpacePosition actualSpacePosition = new SpacePosition();
        String actualToStringResult = actualSpacePosition.toString();
        assertEquals(0, actualSpacePosition.getX());
        assertEquals(0, actualSpacePosition.getY());
        assertEquals("microsim.space.SpacePosition[x=0,y=0]", actualToStringResult);
    }

    @Test
    void testConstructor2() {
        SpacePosition actualSpacePosition = new SpacePosition(2, 3);
        String actualToStringResult = actualSpacePosition.toString();
        assertEquals(2, actualSpacePosition.getX());
        assertEquals(3, actualSpacePosition.getY());
        assertEquals("microsim.space.SpacePosition[x=2,y=3]", actualToStringResult);
    }

    @Test
    void testConstructor3() {
        SpacePosition actualSpacePosition = new SpacePosition(new SpacePosition(2, 3));
        assertEquals(2, actualSpacePosition.getX());
        assertEquals(3, actualSpacePosition.getY());
    }

    @Test
    void testCompareTo() {
        SpacePosition spacePosition = new SpacePosition(2, 3);
        assertEquals(0, spacePosition.compareTo(new SpacePosition(2, 3)));
    }

    @Test
    void testCompareTo2() {
        SpacePosition spacePosition = new SpacePosition(3, 3);
        assertEquals(1, spacePosition.compareTo(new SpacePosition(2, 3)));
    }

    @Test
    void testCompareTo3() {
        SpacePosition spacePosition = new SpacePosition(1, 3);
        assertEquals(-1, spacePosition.compareTo(new SpacePosition(2, 3)));
    }

    @Test
    void testCompareTo4() {
        SpacePosition spacePosition = new SpacePosition(2, 2);
        assertEquals(-1, spacePosition.compareTo(new SpacePosition(2, 3)));
    }

    @Test
    void testEquals() {
        assertThrows(NullPointerException.class, () -> (new SpacePosition(2, 3)).equals(null));
        assertNotEquals(new SpacePosition(2, 3), "Different type to SpacePosition");
    }

    @Test
    void testEquals2() {
        SpacePosition spacePosition = new SpacePosition(2, 3);
        assertEquals(spacePosition, spacePosition);
        int expectedHashCodeResult = spacePosition.hashCode();
        assertEquals(expectedHashCodeResult, spacePosition.hashCode());
    }

    @Test
    void testEquals3() {
        SpacePosition spacePosition = new SpacePosition(2, 3);
        SpacePosition spacePosition1 = new SpacePosition(2, 3);

        assertEquals(spacePosition, spacePosition1);
        int notExpectedHashCodeResult = spacePosition.hashCode();
        assertNotEquals(notExpectedHashCodeResult, spacePosition1.hashCode());
    }

    @Test
    void testEquals4() {
        SpacePosition spacePosition = new SpacePosition(1, 3);
        assertNotEquals(spacePosition, new SpacePosition(2, 3));
    }

    @Test
    void testEquals5() {
        SpacePosition spacePosition = new SpacePosition(2, 1);
        assertNotEquals(spacePosition, new SpacePosition(2, 3));
    }
}

