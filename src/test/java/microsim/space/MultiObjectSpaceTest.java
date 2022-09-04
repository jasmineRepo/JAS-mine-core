package microsim.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiObjectSpaceTest {

    @Test
    void testConstructor() {
        MultiObjectSpace actualMultiObjectSpace = new MultiObjectSpace(3, 3);

        assertEquals(3, actualMultiObjectSpace.getMatrix().length);
        assertEquals(0, actualMultiObjectSpace.modCount);
        assertEquals(0, actualMultiObjectSpace.size());
        assertEquals(3, actualMultiObjectSpace.getYSize());
        assertEquals(3, actualMultiObjectSpace.getXSize());
    }

    @Test
    void testConstructor4() {
        MultiObjectSpace actualMultiObjectSpace = new MultiObjectSpace(3, 3, 1);

        assertEquals(3, actualMultiObjectSpace.getMatrix().length);
        assertEquals(0, actualMultiObjectSpace.modCount);
        assertEquals(0, actualMultiObjectSpace.size());
        assertEquals(3, actualMultiObjectSpace.getYSize());
        assertEquals(3, actualMultiObjectSpace.getXSize());
    }

    @Test
    void testConstructor7() {
        assertThrows(IllegalArgumentException.class, () -> new MultiObjectSpace(3, 3, 0));
    }

    @Test
    void testCountObjectsAt2() {
        assertEquals(0, (new MultiObjectSpace(3, 3)).countObjectsAt(1, 1));
    }

    @Test
    void testCountAt2() {
        assertEquals(0, (new MultiObjectSpace(3, 3)).countAt(1, 1));
    }

    @Test
    void testGet2() {
        assertNull((new MultiObjectSpace(3, 3)).get(1, 1, 1));
    }

    @Test
    void testSet2() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.set(1, 1, 1, "42");
        assertEquals(1, multiObjectSpace.modCount);
        assertEquals(1, multiObjectSpace.size());
    }

    @Test
    void testSet4() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.set(1, 1, 3, "42");
        assertEquals(1, multiObjectSpace.modCount);
        assertEquals(1, multiObjectSpace.size());
    }

    @Test
    void testSet6() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.set(1, 1, 1, null);
        assertEquals(1, multiObjectSpace.modCount);
        assertEquals(-1, multiObjectSpace.size());
    }

    @Test
    void testSet8() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.set(1, 1, "42");
        assertEquals(1, multiObjectSpace.modCount);
        assertEquals(1, multiObjectSpace.size());
    }

    @Test
    void testSet10() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3, 1);
        multiObjectSpace.set(1, 1, "42");
        assertEquals(1, multiObjectSpace.modCount);
        assertEquals(1, multiObjectSpace.size());
    }

    @Test
    void testMoveGridPosition3() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        assertFalse(multiObjectSpace.moveGridPosition(new SpacePosition(2, 1), 1, 1));
    }

    @Test
    void testMoveGridPosition4() {
        assertFalse((new MultiObjectSpace(3, 3)).moveGridPosition(null, 1, 1));
    }

    @Test
    void testMoveGridPosition5() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.addGridPosition(new SpacePosition(2, 1));
        assertFalse(multiObjectSpace.moveGridPosition(new SpacePosition(2, 1), 1, 1));
    }

    @Test
    void testAdd() {
        assertFalse((new MultiObjectSpace(3, 3)).add("42"));
    }

    @Test
    void testAdd3() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        assertTrue(multiObjectSpace.add(new SpacePosition()));
        assertEquals(1, multiObjectSpace.modCount);
        assertEquals(1, multiObjectSpace.size());
    }

    @Test
    void testAdd4() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3, 1);
        assertTrue(multiObjectSpace.add(new SpacePosition()));
        assertEquals(1, multiObjectSpace.modCount);
        assertEquals(1, multiObjectSpace.size());
    }

    @Test
    void testAddGridPosition2() {
        assertFalse((new MultiObjectSpace(3, 3)).addGridPosition(null));
    }

    @Test
    void testAddGridPosition4() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        assertTrue(multiObjectSpace.addGridPosition(new SpacePosition(2, 1)));
        assertEquals(1, multiObjectSpace.modCount);
        assertEquals(1, multiObjectSpace.size());
    }

    @Test
    void testAddGridPosition5() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3, 1);
        assertTrue(multiObjectSpace.addGridPosition(new SpacePosition(2, 1)));
        assertEquals(1, multiObjectSpace.modCount);
        assertEquals(1, multiObjectSpace.size());
    }

    @Test
    void testAddGridPosition6() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.addGridPosition(new SpacePosition(2, 1));
        assertTrue(multiObjectSpace.addGridPosition(new SpacePosition(2, 1)));
        assertEquals(2, multiObjectSpace.modCount);
        assertEquals(2, multiObjectSpace.size());
    }

    @Test
    void testAddGridPosition7() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3, 1);
        multiObjectSpace.addGridPosition(new SpacePosition(2, 1));
        assertTrue(multiObjectSpace.addGridPosition(new SpacePosition(2, 1)));
        assertEquals(2, multiObjectSpace.modCount);
        assertEquals(2, multiObjectSpace.size());
    }

    @Test
    void testRemove() {
        assertFalse((new MultiObjectSpace(3, 3)).remove("42"));
    }

    @Test
    void testRemove2() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.addGridPosition(new SpacePosition(2, 1));
        assertFalse(multiObjectSpace.remove("42"));
    }

    @Test
    void testRemove3() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.addGridPosition(new SpacePosition(2, 1));
        assertTrue(multiObjectSpace.remove(null));
        assertEquals(2, multiObjectSpace.modCount);
        assertEquals(0, multiObjectSpace.size());
    }

    @Test
    void testRemoveGridPosition3() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        assertFalse(multiObjectSpace.removeGridPosition(new SpacePosition(2, 1)));
    }

    @Test
    void testRemoveGridPosition4() {
        assertFalse((new MultiObjectSpace(3, 3)).removeGridPosition(null));
    }

    @Test
    void testRemoveGridPosition5() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.addGridPosition(new SpacePosition(2, 1));
        assertFalse(multiObjectSpace.removeGridPosition(new SpacePosition(2, 1)));
    }

    @Test
    void testRemoveAt2() {
        assertFalse((new MultiObjectSpace(3, 3)).removeAt(1, 1, "42"));
    }
}

