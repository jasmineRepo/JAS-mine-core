package microsim.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DenseObjectSpaceTest {

    @Test
    void testConstructor() {
        DenseObjectSpace actualDenseObjectSpace = new DenseObjectSpace(3, 3);

        Object[][] expectedMatrix = actualDenseObjectSpace.m;
        assertSame(expectedMatrix, actualDenseObjectSpace.getMatrix());
        assertEquals(0, actualDenseObjectSpace.size());
    }

    @Test
    void testConstructor1() {
        DenseObjectSpace actualDenseObjectSpace = new DenseObjectSpace(3, 3);

        assertEquals(3, actualDenseObjectSpace.getMatrix().length);
        assertEquals(0, actualDenseObjectSpace.modCount);
        assertEquals(0, actualDenseObjectSpace.size());
        assertEquals(3, actualDenseObjectSpace.getYSize());
        assertEquals(3, actualDenseObjectSpace.getXSize());
    }

    @Test
    void testConstructor2() {
        DenseObjectSpace actualDenseObjectSpace = new DenseObjectSpace(new DenseObjectSpace(3, 3));
        assertEquals(3, actualDenseObjectSpace.getMatrix().length);
        assertEquals(9, actualDenseObjectSpace.modCount);
        assertEquals(0, actualDenseObjectSpace.size());
        assertEquals(3, actualDenseObjectSpace.getYSize());
        assertEquals(3, actualDenseObjectSpace.getXSize());
    }

    @Test
    void testConstructor3() {
        DenseObjectSpace actualDenseObjectSpace = new DenseObjectSpace(new SparseObjectSpace(3, 3));
        assertEquals(3, actualDenseObjectSpace.getMatrix().length);
        assertEquals(9, actualDenseObjectSpace.modCount);
        assertEquals(0, actualDenseObjectSpace.size());
        assertEquals(3, actualDenseObjectSpace.getYSize());
        assertEquals(3, actualDenseObjectSpace.getXSize());
    }

    @Test
    void testConstructor4() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        denseObjectSpace.addGridPosition(new SpacePosition(2, 1));
        DenseObjectSpace actualDenseObjectSpace = new DenseObjectSpace(denseObjectSpace);
        assertEquals(3, actualDenseObjectSpace.getMatrix().length);
        assertEquals(9, actualDenseObjectSpace.modCount);
        assertEquals(1, actualDenseObjectSpace.size());
        assertEquals(3, actualDenseObjectSpace.getYSize());
        assertEquals(3, actualDenseObjectSpace.getXSize());
    }

    @Test
    void testGet() {
        assertNull((new DenseObjectSpace(3, 3)).get(1, 1));
    }

    @Test
    void testAdd() {
        assertFalse((new DenseObjectSpace(3, 3)).add("42"));
    }

    @Test
    void testAdd1() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        assertTrue(denseObjectSpace.add(new SpacePosition()));
        assertEquals(2, denseObjectSpace.modCount);
        assertEquals(1, denseObjectSpace.size());
    }

    @Test
    void testSwapPositions() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        denseObjectSpace.swapPositions(1, 1, 1, 1);
        assertEquals(3, denseObjectSpace.modCount);
    }

    @Test
    void testSwapPositions1() {
        MultiObjectSpace multiObjectSpace = new MultiObjectSpace(3, 3);
        multiObjectSpace.swapPositions(1, 1, 1, 1);
        assertEquals(3, multiObjectSpace.modCount);
        assertEquals(2, multiObjectSpace.size());
    }

    @Test
    void testSet() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        denseObjectSpace.set(1, 1, "42");
        assertEquals(1, denseObjectSpace.modCount);
        assertEquals(1, denseObjectSpace.size());
    }

    @Test
    void testSet1() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        denseObjectSpace.set(1, 1, null);
        assertEquals(1, denseObjectSpace.modCount);
    }

    @Test
    void testGetMooreNeighbors() {
        assertEquals(8, (new DenseObjectSpace(3, 3)).getMooreNeighbors(2, 3).length);
        assertEquals(8, (new DenseObjectSpace(3, 3)).getMooreNeighbors(0, 3).length);
        assertEquals(8, (new DenseObjectSpace(3, 3)).getMooreNeighbors(2, 0).length);
        assertEquals(8, (new DenseObjectSpace(3, 3)).getMooreNeighbors(2, 3, 1, 1).length);
        assertEquals(8, (new DenseObjectSpace(3, 3)).getMooreNeighbors(0, 3, 1, 1).length);
        assertEquals(8, (new DenseObjectSpace(3, 3)).getMooreNeighbors(2, 0, 1, 1).length);
        assertThrows(NegativeArraySizeException.class,
            () -> (new DenseObjectSpace(3, 3)).getMooreNeighbors(2, 3, -1, 1));
    }

    @Test
    void testGetVonNeumannNeighbors() {
        assertEquals(4, (new DenseObjectSpace(3, 3)).getVonNeumannNeighbors(2, 3).length);
        assertEquals(4, (new DenseObjectSpace(3, 3)).getVonNeumannNeighbors(0, 3).length);
        assertEquals(4, (new DenseObjectSpace(3, 3)).getVonNeumannNeighbors(1, 1, 1, 1).length);
        assertThrows(NegativeArraySizeException.class,
            () -> (new DenseObjectSpace(3, 3)).getVonNeumannNeighbors(-1, 3, -2, 1));
    }

    @Test
    void testMoveGridPosition() {
        assertFalse((new DenseObjectSpace(3, 3)).moveGridPosition(null, 1, 1));
    }

    @Test
    void testMoveGridPosition1() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        assertTrue(denseObjectSpace.moveGridPosition(new SpacePosition(2, 1), 1, 1));
        assertEquals(1, denseObjectSpace.modCount);
    }

    @Test
    void testClear() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        denseObjectSpace.clear();
        assertEquals(1, denseObjectSpace.modCount);
        assertEquals(0, denseObjectSpace.size());
    }

    @Test
    void testAddGridPosition() {
        assertFalse((new DenseObjectSpace(3, 3)).addGridPosition(null));
    }

    @Test
    void testAddGridPosition1() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        assertTrue(denseObjectSpace.addGridPosition(new SpacePosition(2, 1)));
        assertEquals(2, denseObjectSpace.modCount);
        assertEquals(1, denseObjectSpace.size());
    }


    @Test
    void testAddGridPosition2() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        denseObjectSpace.addGridPosition(new SpacePosition(2, 1));
        assertFalse(denseObjectSpace.addGridPosition(new SpacePosition(2, 1)));
    }

    @Test
    void testRemoveGridPosition() {
        assertFalse((new DenseObjectSpace(3, 3)).removeGridPosition(null));
    }

    @Test
    void testRemoveGridPosition1() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        assertFalse(denseObjectSpace.removeGridPosition(new SpacePosition(2, 1)));
    }

    @Test
    void testRemoveGridPosition2() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        denseObjectSpace.addGridPosition(new SpacePosition(2, 1));
        assertTrue(denseObjectSpace.removeGridPosition(new SpacePosition(2, 1)));
        assertEquals(3, denseObjectSpace.modCount);
        assertEquals(0, denseObjectSpace.size());
    }

    @Test
    void testCountObjectsAt() {
        assertEquals(0, (new DenseObjectSpace(3, 3)).countObjectsAt(1, 1));
    }
}

