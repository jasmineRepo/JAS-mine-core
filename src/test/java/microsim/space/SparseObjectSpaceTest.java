package microsim.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SparseObjectSpaceTest {

    @Test
    void testConstructor() {
        SparseObjectSpace actualSparseObjectSpace = new SparseObjectSpace(3, 3);

        assertEquals(3, actualSparseObjectSpace.getXSize());
        assertEquals(0, actualSparseObjectSpace.modCount);
        assertTrue(actualSparseObjectSpace.m.isEmpty());
        assertEquals(3, actualSparseObjectSpace.getYSize());
    }

    @Test
    void testConstructor4() {
        var dos =new DenseObjectSpace(3, 3);

        var actualSparseObjectSpace = new SparseObjectSpace(new DenseObjectSpace(3, 3));
        assertEquals(3, actualSparseObjectSpace.getXSize());
        assertEquals(0, actualSparseObjectSpace.modCount);
        assertEquals(9, actualSparseObjectSpace.m.size());
        assertEquals(3, actualSparseObjectSpace.getYSize());
    }

    @Test
    void testConstructor5() {
        SparseObjectSpace actualSparseObjectSpace = new SparseObjectSpace(new SparseObjectSpace(3, 3));
        assertEquals(3, actualSparseObjectSpace.getXSize());
        assertEquals(0, actualSparseObjectSpace.modCount);
        assertEquals(9, actualSparseObjectSpace.m.size());
        assertEquals(3, actualSparseObjectSpace.getYSize());
    }

    @Test
    void testGet() {
        assertNull((new SparseObjectSpace(3, 3)).get(2, 3));
    }

    @Test
    void testSet() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        sparseObjectSpace.set(2, 3, "Obj");
        assertEquals(1, sparseObjectSpace.m.size());
    }

    @Test
    void testSwapPositions() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        sparseObjectSpace.swapPositions(1, 3, 1, 1);
        assertEquals(2, sparseObjectSpace.m.size());
    }

    @Test
    void testCountObjectsAt() {
        assertEquals(0, (new SparseObjectSpace(3, 3)).countObjectsAt(2, 3));
        assertEquals(0, (new SparseObjectSpace(3, 3)).countObjectsAt(1, 1));
        assertEquals(0, (new SparseObjectSpace(3, 3)).countObjectsAt(3, 3));
        assertEquals(0, (new SparseObjectSpace(3, 3)).countObjectsAt(1, 3));
    }

    @Test
    void testCountObjectsAt2() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        sparseObjectSpace.addGridPosition(new SpacePosition(2, 3));
        assertEquals(1, sparseObjectSpace.countObjectsAt(2, 3));
    }

    @Test
    void testMoveGridPosition() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        assertTrue(sparseObjectSpace.moveGridPosition(new SpacePosition(2, 3), 1, 1));
        assertEquals(1, sparseObjectSpace.modCount);
        assertEquals(1, sparseObjectSpace.m.size());
    }

    @Test
    void testMoveGridPosition2() {
        assertFalse((new SparseObjectSpace(3, 3)).moveGridPosition(null, 1, 1));
    }

    @Test
    void testSize() {
        assertEquals(0, (new SparseObjectSpace(3, 3)).size());
    }

    @Test
    void testContains() {
        assertFalse((new SparseObjectSpace(3, 3)).contains("42"));
    }

    @Test
    void testToArray() {
        assertEquals(0, (new SparseObjectSpace(3, 3)).toArray().length);
    }

    @Test
    void testAdd() {
        assertFalse((new SparseObjectSpace(3, 3)).add("42"));
    }

    @Test
    void testAdd2() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        assertTrue(sparseObjectSpace.add(new SpacePosition(2, 3)));
        assertEquals(1, sparseObjectSpace.m.size());
    }

    @Test
    void testRemove() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        assertTrue(sparseObjectSpace.remove(new SpacePosition(2, 3)));
        assertTrue(sparseObjectSpace.m.isEmpty());
    }

    @Test
    void testClear() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        sparseObjectSpace.clear();
        assertTrue(sparseObjectSpace.m.isEmpty());
    }

    @Test
    void testAddGridPosition() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        assertTrue(sparseObjectSpace.addGridPosition(new SpacePosition(2, 3)));
        assertEquals(1, sparseObjectSpace.m.size());
    }

    @Test
    void testAddGridPosition2() {
        assertFalse((new SparseObjectSpace(3, 3)).addGridPosition(null));
    }

    @Test
    void testAddGridPosition3() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        sparseObjectSpace.addGridPosition(new SpacePosition(2, 3));
        assertFalse(sparseObjectSpace.addGridPosition(new SpacePosition(2, 3)));
    }

    @Test
    void testRemoveGridPosition() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        assertTrue(sparseObjectSpace.removeGridPosition(new SpacePosition(2, 3)));
        assertTrue(sparseObjectSpace.m.isEmpty());
    }

    @Test
    void testRemoveGridPosition2() {
        assertFalse((new SparseObjectSpace(3, 3)).removeGridPosition(null));
    }

    @Test
    void testRemoveGridPosition3() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        sparseObjectSpace.addGridPosition(new SpacePosition(2, 3));
        assertFalse(sparseObjectSpace.removeGridPosition(new SpacePosition(2, 3)));
    }

    @Test
    void testEquals() {
        assertThrows(NullPointerException.class, () -> (new SparseObjectSpace(3, 3)).equals(null));
        assertNotEquals(new SparseObjectSpace(3, 3), "Different type to SparseObjectSpace");
    }

    @Test
    void testEquals2() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        assertEquals(sparseObjectSpace, sparseObjectSpace);
        int expectedHashCodeResult = sparseObjectSpace.hashCode();
        assertEquals(expectedHashCodeResult, sparseObjectSpace.hashCode());
    }

    @Test
    void testEquals3() {
        SparseObjectSpace sparseObjectSpace = new SparseObjectSpace(3, 3);
        assertEquals(sparseObjectSpace, new SparseObjectSpace(3, 3));
    }
}

