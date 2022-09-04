package microsim.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class IntSpaceTest {

    @Test
    void testConstructor() {
        IntSpace actualIntSpace = new IntSpace(3, 3);

        int[] expectedMatrix = actualIntSpace.m;
        assertSame(expectedMatrix, actualIntSpace.getMatrix());
        assertEquals(9, actualIntSpace.size());
    }

    @Test
    void testConstructor2() {
        IntSpace actualIntSpace = new IntSpace(3, 3);

        assertEquals(9, actualIntSpace.getMatrix().length);
        assertEquals(0, actualIntSpace.modCount);
        assertEquals(9, actualIntSpace.size());
        assertEquals(3, actualIntSpace.getYSize());
        assertEquals(3, actualIntSpace.getXSize());
    }

    @Test
    void testConstructor5() {
        IntSpace actualIntSpace = new IntSpace(new IntSpace(3, 3));
        assertEquals(9, actualIntSpace.getMatrix().length);
        assertEquals(0, actualIntSpace.modCount);
        assertEquals(0.0d, actualIntSpace.variance());
        assertEquals(9, actualIntSpace.size());
        assertEquals(3, actualIntSpace.getYSize());
        assertEquals(3, actualIntSpace.getXSize());
    }

    @Test
    void testGet2() {
        assertEquals(0, (new IntSpace(3, 3)).get(1, 1).intValue());
    }

    @Test
    void testGetInt2() {
        assertEquals(0, (new IntSpace(3, 3)).getInt(1, 1));
    }

    @Test
    void testSet() {
        IntSpace intSpace = new IntSpace(3, 3);
        intSpace.set(1, 1, null);
        assertEquals(0.0d, intSpace.variance());
    }

    @Test
    void testSet2() {
        IntSpace intSpace = new IntSpace(3, 3);
        intSpace.set(1, 1, 1);
        assertEquals(0.09876543209876545d, intSpace.variance());
    }

    @Test
    void testSwapPositions2() {
        IntSpace intSpace = new IntSpace(3, 3);
        intSpace.swapPositions(1, 1, 1, 1);
        assertEquals(0.0d, intSpace.variance());
    }

    @Test
    void testSetInt2() {
        IntSpace intSpace = new IntSpace(3, 3);
        intSpace.setInt(1, 1, 1);
        assertEquals(0.09876543209876545d, intSpace.variance());
    }

    @Test
    void testClear() {
        IntSpace intSpace = new IntSpace(3, 3);
        intSpace.clear();
        assertEquals(0.0d, intSpace.variance());
    }

    @Test
    void testResetTo() {
        IntSpace intSpace = new IntSpace(3, 3);
        intSpace.resetTo(42);
        assertEquals(0.0d, intSpace.variance());
    }

    @Test
    void testAdd() {
        IntSpace intSpace = new IntSpace(3, 3);
        intSpace.add(2);
        assertEquals(0.0d, intSpace.variance());
    }

    @Test
    void testMultiply() {
        IntSpace intSpace = new IntSpace(3, 3);
        intSpace.multiply(1);
        assertEquals(0.0d, intSpace.variance());
    }

    @Test
    void testMin() {
        assertEquals(0, (new IntSpace(3, 3)).min());
    }

    @Test
    void testMax() {
        assertEquals(0, (new IntSpace(3, 3)).max());
    }

    @Test
    void testSum() {
        assertEquals(0, (new IntSpace(3, 3)).sum());
    }

    @Test
    void testMean() {
        assertEquals(0.0d, (new IntSpace(3, 3)).mean());
    }

    @Test
    void testVariance() {
        assertEquals(0.0d, (new IntSpace(3, 3)).variance());
    }

    @Test
    void testCopyGridTo() {
        IntSpace intSpace = new IntSpace(3, 3);
        IntSpace intSpace1 = new IntSpace(3, 3);

        intSpace.copyGridTo(intSpace1);
        assertEquals(9, intSpace1.getMatrix().length);
    }

    @Test
    void testCopyGridTo4() {
        IntSpace intSpace = new IntSpace(1, 3);
        intSpace.copyGridTo(new int[]{1, 1, 1, 1});
        assertEquals(3, intSpace.getMatrix().length);
        assertEquals(0, intSpace.modCount);
        assertEquals(3, intSpace.size());
        assertEquals(3, intSpace.getYSize());
        assertEquals(1, intSpace.getXSize());
    }

    @Test
    void testCountObjectsAt2() {
        assertEquals(0, (new IntSpace(3, 3)).countObjectsAt(1, 1));
    }
}

