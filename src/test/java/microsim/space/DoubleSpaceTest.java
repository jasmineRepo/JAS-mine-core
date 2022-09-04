package microsim.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DoubleSpaceTest {

    @Test
    void testConstructor() {
        DoubleSpace actualDoubleSpace = new DoubleSpace(3, 3);

        double[] expectedMatrix = actualDoubleSpace.m;
        assertSame(expectedMatrix, actualDoubleSpace.getMatrix());
        assertEquals(9, actualDoubleSpace.size());
    }

    @Test
    void testConstructor2() {
        DoubleSpace actualDoubleSpace = new DoubleSpace(3, 3);

        assertEquals(9, actualDoubleSpace.getMatrix().length);
        assertEquals(0, actualDoubleSpace.modCount);
        assertEquals(9, actualDoubleSpace.size());
        assertEquals(3, actualDoubleSpace.getYSize());
        assertEquals(3, actualDoubleSpace.getXSize());
    }

    @Test
    void testConstructor5() {
        DoubleSpace actualDoubleSpace = new DoubleSpace(new DoubleSpace(3, 3));
        assertEquals(9, actualDoubleSpace.getMatrix().length);
        assertEquals(0, actualDoubleSpace.modCount);
        assertEquals(0.0d, actualDoubleSpace.variance());
        assertEquals(9, actualDoubleSpace.size());
        assertEquals(3, actualDoubleSpace.getYSize());
        assertEquals(3, actualDoubleSpace.getXSize());
    }

    @Test
    void testAt() {
        assertEquals(11, (new DoubleSpace(3, 3)).at(2, 3));
        assertEquals(4, (new DoubleSpace(3, 3)).at(1, 1));
        assertEquals(12, (new DoubleSpace(3, 3)).at(3, 3));
        assertEquals(10, (new DoubleSpace(3, 3)).at(1, 3));
    }

    @Test
    void testGet2() {
        assertEquals(0.0d, (new DoubleSpace(3, 3)).get(1, 1).doubleValue());
    }

    @Test
    void testGetDbl2() {
        assertEquals(0.0d, (new DoubleSpace(3, 3)).getDbl(1, 1));
    }

    @Test
    void testSet() {
        DoubleSpace doubleSpace = new DoubleSpace(3, 3);
        doubleSpace.set(1, 1, null);
        assertEquals(0.0d, doubleSpace.variance());
    }

    @Test
    void testSet2() {
        DoubleSpace doubleSpace = new DoubleSpace(3, 3);
        doubleSpace.set(1, 1, 1);
        assertEquals(0.09876543209876545d, doubleSpace.variance());
    }

    @Test
    void testSwapPositions2() {
        DoubleSpace doubleSpace = new DoubleSpace(3, 3);
        doubleSpace.swapPositions(1, 1, 1, 1);
        assertEquals(0.0d, doubleSpace.variance());
    }

    @Test
    void testSetDbl2() {
        DoubleSpace doubleSpace = new DoubleSpace(3, 3);
        doubleSpace.setDbl(1, 1, 10.0d);
        assertEquals(9.876543209876546d, doubleSpace.variance());
    }

    @Test
    void testClear() {
        DoubleSpace doubleSpace = new DoubleSpace(3, 3);
        doubleSpace.clear();
        assertEquals(0.0d, doubleSpace.variance());
    }

    @Test
    void testResetTo() {
        DoubleSpace doubleSpace = new DoubleSpace(3, 3);
        doubleSpace.resetTo(10.0d);
        assertEquals(0.0d, doubleSpace.variance());
    }

    @Test
    void testAdd() {
        DoubleSpace doubleSpace = new DoubleSpace(3, 3);
        doubleSpace.add(10.0d);
        assertEquals(0.0d, doubleSpace.variance());
    }

    @Test
    void testMultiply() {
        DoubleSpace doubleSpace = new DoubleSpace(3, 3);
        doubleSpace.multiply(10.0d);
        assertEquals(0.0d, doubleSpace.variance());
    }

    @Test
    void testMin() {
        assertEquals(0.0d, (new DoubleSpace(3, 3)).min());
    }

    @Test
    void testMax() {
        assertEquals(0.0d, (new DoubleSpace(3, 3)).max());
    }

    @Test
    void testSum() {
        assertEquals(0.0d, (new DoubleSpace(3, 3)).sum());
    }

    @Test
    void testMean() {
        assertEquals(0.0d, (new DoubleSpace(3, 3)).mean());
    }

    @Test
    void testVariance() {
        assertEquals(0.0d, (new DoubleSpace(3, 3)).variance());
    }

    @Test
    void testCopyGridTo() {
        DoubleSpace doubleSpace = new DoubleSpace(3, 3);
        DoubleSpace doubleSpace1 = new DoubleSpace(3, 3);

        doubleSpace.copyGridTo(doubleSpace1);
        assertEquals(9, doubleSpace1.getMatrix().length);
        assertEquals(0, doubleSpace1.modCount);
        assertEquals(9, doubleSpace1.size());
        assertEquals(3, doubleSpace1.getYSize());
        assertEquals(3, doubleSpace1.getXSize());
        assertEquals(9, doubleSpace.getMatrix().length);
        assertEquals(0, doubleSpace.modCount);
        assertEquals(9, doubleSpace.size());
        assertEquals(3, doubleSpace.getYSize());
        assertEquals(3, doubleSpace.getXSize());
    }

    @Test
    void testCopyGridTo5() {
        DoubleSpace doubleSpace = new DoubleSpace(1, 3);
        doubleSpace.copyGridTo(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(3, doubleSpace.getMatrix().length);
        assertEquals(0, doubleSpace.modCount);
        assertEquals(3, doubleSpace.size());
        assertEquals(3, doubleSpace.getYSize());
        assertEquals(1, doubleSpace.getXSize());
    }

    @Test
    void testCountObjectsAt2() {
        assertEquals(0, (new DoubleSpace(3, 3)).countObjectsAt(1, 1));
    }
}

