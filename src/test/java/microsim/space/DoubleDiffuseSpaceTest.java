package microsim.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DoubleDiffuseSpaceTest {

    @Test
    void testConstructor() {
        DoubleDiffuseSpace actualDoubleDiffuseSpace = new DoubleDiffuseSpace(3, 3);
        actualDoubleDiffuseSpace.setDiffusionConstant(10.0d);
        actualDoubleDiffuseSpace.setEvaporationRate(10.0d);
        assertEquals(9, actualDoubleDiffuseSpace.getMatrix().length);
        assertEquals(0, actualDoubleDiffuseSpace.modCount);
        assertEquals(0.0d, actualDoubleDiffuseSpace.variance());
        assertEquals(0.0d, actualDoubleDiffuseSpace.sum());
        assertEquals(9, actualDoubleDiffuseSpace.size());
        assertEquals(0.0d, actualDoubleDiffuseSpace.min());
        assertEquals(0.0d, actualDoubleDiffuseSpace.mean());
        assertEquals(0.0d, actualDoubleDiffuseSpace.max());
        assertEquals(3, actualDoubleDiffuseSpace.getYSize());
        assertEquals(3, actualDoubleDiffuseSpace.getXSize());
    }

    @Test
    void testConstructor2() {
        DoubleDiffuseSpace actualDoubleDiffuseSpace = new DoubleDiffuseSpace(3, 3);

        assertEquals(9, actualDoubleDiffuseSpace.getMatrix().length);
        assertEquals(0, actualDoubleDiffuseSpace.modCount);
        assertEquals(0.0d, actualDoubleDiffuseSpace.variance());
        assertEquals(9, actualDoubleDiffuseSpace.size());
        assertEquals(3, actualDoubleDiffuseSpace.getYSize());
        assertEquals(3, actualDoubleDiffuseSpace.getXSize());
    }

    @Test
    void testConstructor5() {
        DoubleDiffuseSpace actualDoubleDiffuseSpace = new DoubleDiffuseSpace(3, 3, 10.0d, 10.0d);

        assertEquals(9, actualDoubleDiffuseSpace.getMatrix().length);
        assertEquals(0, actualDoubleDiffuseSpace.modCount);
        assertEquals(0.0d, actualDoubleDiffuseSpace.variance());
        assertEquals(9, actualDoubleDiffuseSpace.size());
        assertEquals(3, actualDoubleDiffuseSpace.getYSize());
        assertEquals(3, actualDoubleDiffuseSpace.getXSize());
    }

    @Test
    void testDiffuse() {
        DoubleDiffuseSpace doubleDiffuseSpace = new DoubleDiffuseSpace(3, 3);
        doubleDiffuseSpace.diffuse();
        assertEquals(0.0d, doubleDiffuseSpace.variance());
    }

    @Test
    void testUpdate() {
        DoubleDiffuseSpace doubleDiffuseSpace = new DoubleDiffuseSpace(3, 3);
        doubleDiffuseSpace.update();
        assertEquals(9, doubleDiffuseSpace.getMatrix().length);
        assertEquals(0, doubleDiffuseSpace.modCount);
        assertEquals(0.0d, doubleDiffuseSpace.variance());
        assertEquals(9, doubleDiffuseSpace.size());
        assertEquals(3, doubleDiffuseSpace.getYSize());
        assertEquals(3, doubleDiffuseSpace.getXSize());
    }

    @Test
    void testPrint2() {
        DoubleDiffuseSpace doubleDiffuseSpace = new DoubleDiffuseSpace(1, 3);
        doubleDiffuseSpace.print(new double[]{10.0d, 10.0d, 10.0d, 10.0d});
        assertEquals(3, doubleDiffuseSpace.getMatrix().length);
        assertEquals(0, doubleDiffuseSpace.modCount);
        assertEquals(0.0d, doubleDiffuseSpace.variance());
        assertEquals(3, doubleDiffuseSpace.size());
        assertEquals(3, doubleDiffuseSpace.getYSize());
        assertEquals(1, doubleDiffuseSpace.getXSize());
    }

    @Test
    void testGetVonNeumannNeighbors2() {
        double[] actualVonNeumannNeighbors = (new DoubleDiffuseSpace(3, 3)).getVonNeumannNeighbors(1, 1);
        assertEquals(4, actualVonNeumannNeighbors.length);
        assertEquals(0.0d, actualVonNeumannNeighbors[0]);
        assertEquals(0.0d, actualVonNeumannNeighbors[1]);
        assertEquals(0.0d, actualVonNeumannNeighbors[2]);
        assertEquals(0.0d, actualVonNeumannNeighbors[3]);
    }

    @Test
    void testGetVonNeumannNeighbors9() {
        double[] actualVonNeumannNeighbors = (new DoubleDiffuseSpace(3, 3)).getVonNeumannNeighbors(1, 1, 1, 1);
        assertEquals(4, actualVonNeumannNeighbors.length);
        assertEquals(0.0d, actualVonNeumannNeighbors[0]);
        assertEquals(0.0d, actualVonNeumannNeighbors[1]);
        assertEquals(0.0d, actualVonNeumannNeighbors[2]);
        assertEquals(0.0d, actualVonNeumannNeighbors[3]);
    }

    @Test
    void testGetVonNeumannNeighbors18() {
        assertThrows(NegativeArraySizeException.class,
            () -> (new DoubleDiffuseSpace(3, 3)).getVonNeumannNeighbors(-1, 3, -2, 1));
    }

    @Test
    void testGetMooreNeighbors() {
        double[] actualMooreNeighbors = (new DoubleDiffuseSpace(3, 3)).getMooreNeighbors(2, 3);
        assertEquals(8, actualMooreNeighbors.length);
        assertEquals(0.0d, actualMooreNeighbors[0]);
        assertEquals(0.0d, actualMooreNeighbors[1]);
        assertEquals(0.0d, actualMooreNeighbors[2]);
        assertEquals(0.0d, actualMooreNeighbors[3]);
        assertEquals(0.0d, actualMooreNeighbors[4]);
        assertEquals(0.0d, actualMooreNeighbors[5]);
        assertEquals(0.0d, actualMooreNeighbors[6]);
        assertEquals(0.0d, actualMooreNeighbors[7]);
    }

    @Test
    void testGetMooreNeighbors2() {
        double[] actualMooreNeighbors = (new DoubleDiffuseSpace(3, 3)).getMooreNeighbors(0, 3);
        assertEquals(8, actualMooreNeighbors.length);
        assertEquals(0.0d, actualMooreNeighbors[0]);
        assertEquals(0.0d, actualMooreNeighbors[1]);
        assertEquals(0.0d, actualMooreNeighbors[2]);
        assertEquals(0.0d, actualMooreNeighbors[3]);
        assertEquals(0.0d, actualMooreNeighbors[4]);
        assertEquals(0.0d, actualMooreNeighbors[5]);
        assertEquals(0.0d, actualMooreNeighbors[6]);
        assertEquals(0.0d, actualMooreNeighbors[7]);
    }

    @Test
    void testGetMooreNeighbors3() {
        double[] actualMooreNeighbors = (new DoubleDiffuseSpace(3, 3)).getMooreNeighbors(2, 0);
        assertEquals(8, actualMooreNeighbors.length);
        assertEquals(0.0d, actualMooreNeighbors[0]);
        assertEquals(0.0d, actualMooreNeighbors[1]);
        assertEquals(0.0d, actualMooreNeighbors[2]);
        assertEquals(0.0d, actualMooreNeighbors[3]);
        assertEquals(0.0d, actualMooreNeighbors[4]);
        assertEquals(0.0d, actualMooreNeighbors[5]);
        assertEquals(0.0d, actualMooreNeighbors[6]);
        assertEquals(0.0d, actualMooreNeighbors[7]);
    }

    @Test
    void testGetMooreNeighbors4() {
        double[] actualMooreNeighbors = (new DoubleDiffuseSpace(3, 3)).getMooreNeighbors(2, 3, 1, 1);
        assertEquals(8, actualMooreNeighbors.length);
        assertEquals(0.0d, actualMooreNeighbors[0]);
        assertEquals(0.0d, actualMooreNeighbors[1]);
        assertEquals(0.0d, actualMooreNeighbors[2]);
        assertEquals(0.0d, actualMooreNeighbors[3]);
        assertEquals(0.0d, actualMooreNeighbors[4]);
        assertEquals(0.0d, actualMooreNeighbors[5]);
        assertEquals(0.0d, actualMooreNeighbors[6]);
        assertEquals(0.0d, actualMooreNeighbors[7]);
    }

    @Test
    void testGetMooreNeighbors5() {
        double[] actualMooreNeighbors = (new DoubleDiffuseSpace(3, 3)).getMooreNeighbors(0, 3, 1, 1);
        assertEquals(8, actualMooreNeighbors.length);
        assertEquals(0.0d, actualMooreNeighbors[0]);
        assertEquals(0.0d, actualMooreNeighbors[1]);
        assertEquals(0.0d, actualMooreNeighbors[2]);
        assertEquals(0.0d, actualMooreNeighbors[3]);
        assertEquals(0.0d, actualMooreNeighbors[4]);
        assertEquals(0.0d, actualMooreNeighbors[5]);
        assertEquals(0.0d, actualMooreNeighbors[6]);
        assertEquals(0.0d, actualMooreNeighbors[7]);
    }

    @Test
    void testGetMooreNeighbors6() {
        double[] actualMooreNeighbors = (new DoubleDiffuseSpace(3, 3)).getMooreNeighbors(2, 0, 1, 1);
        assertEquals(8, actualMooreNeighbors.length);
        assertEquals(0.0d, actualMooreNeighbors[0]);
        assertEquals(0.0d, actualMooreNeighbors[1]);
        assertEquals(0.0d, actualMooreNeighbors[2]);
        assertEquals(0.0d, actualMooreNeighbors[3]);
        assertEquals(0.0d, actualMooreNeighbors[4]);
        assertEquals(0.0d, actualMooreNeighbors[5]);
        assertEquals(0.0d, actualMooreNeighbors[6]);
        assertEquals(0.0d, actualMooreNeighbors[7]);
    }

    /**
     * Method under test: {@link DoubleDiffuseSpace#getMooreNeighbors(int, int, int, int)}
     */
    @Test
    void testGetMooreNeighbors7() {
        assertThrows(NegativeArraySizeException.class,
            () -> (new DoubleDiffuseSpace(3, 3)).getMooreNeighbors(2, 3, -1, 1));
    }

    @Test
    void testFindMaximum2() {
        double[] actualFindMaximumResult = (new DoubleDiffuseSpace(3, 3)).findMaximum(1, 1, 1, true, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.MOORE);
        assertEquals(2, actualFindMaximumResult.length);
        assertEquals(0.0d, actualFindMaximumResult[0]);
        assertEquals(0.0d, actualFindMaximumResult[1]);
    }

    @Test
    void testFindMaximum4() {
        double[] actualFindMaximumResult = (new DoubleDiffuseSpace(3, 3)).findMaximum(2, 0, 1, true, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.MOORE);
        assertEquals(2, actualFindMaximumResult.length);
        assertEquals(0.0d, actualFindMaximumResult[0]);
        assertEquals(0.0d, actualFindMaximumResult[1]);
    }

    @Test
    void testFindMaximum6() {
        double[] actualFindMaximumResult = (new DoubleDiffuseSpace(3, 3)).findMaximum(2, 3, 1, false, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.MOORE);
        assertEquals(2, actualFindMaximumResult.length);
        assertEquals(0.0d, actualFindMaximumResult[0]);
        assertEquals(0.0d, actualFindMaximumResult[1]);
    }

    @Test
    void testFindMaximum12() {
        assertEquals(0, (new DoubleDiffuseSpace(3, 3)).findMaximum(2, 3, 0, false, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.MOORE).length);
    }

    @Test
    void testFindMaximum13() {
        double[] actualFindMaximumResult = (new DoubleDiffuseSpace(3, 3)).findMaximum(2, 1, 1, true, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.VON_NEUMANN);
        assertEquals(2, actualFindMaximumResult.length);
        assertEquals(0.0d, actualFindMaximumResult[0]);
        assertEquals(0.0d, actualFindMaximumResult[1]);
    }

    @Test
    void testFindMinimum2() {
        double[] actualFindMinimumResult = (new DoubleDiffuseSpace(3, 3)).findMinimum(1, 1, 1, true, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.MOORE);
        assertEquals(9, actualFindMinimumResult.length);
        assertEquals(0.0d, actualFindMinimumResult[0]);
        assertEquals(0.0d, actualFindMinimumResult[1]);
        assertEquals(0.0d, actualFindMinimumResult[2]);
        assertEquals(0.0d, actualFindMinimumResult[3]);
        assertEquals(0.0d, actualFindMinimumResult[4]);
        assertEquals(0.0d, actualFindMinimumResult[5]);
        assertEquals(0.0d, actualFindMinimumResult[6]);
        assertEquals(0.0d, actualFindMinimumResult[7]);
        assertEquals(0.0d, actualFindMinimumResult[8]);
    }

    @Test
    void testFindMinimum4() {
        double[] actualFindMinimumResult = (new DoubleDiffuseSpace(3, 3)).findMinimum(2, 0, 1, true, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.MOORE);
        assertEquals(9, actualFindMinimumResult.length);
        assertEquals(0.0d, actualFindMinimumResult[0]);
        assertEquals(0.0d, actualFindMinimumResult[1]);
        assertEquals(0.0d, actualFindMinimumResult[2]);
        assertEquals(0.0d, actualFindMinimumResult[3]);
        assertEquals(0.0d, actualFindMinimumResult[4]);
        assertEquals(0.0d, actualFindMinimumResult[5]);
        assertEquals(0.0d, actualFindMinimumResult[6]);
        assertEquals(0.0d, actualFindMinimumResult[7]);
        assertEquals(0.0d, actualFindMinimumResult[8]);
    }

    @Test
    void testFindMinimum6() {
        double[] actualFindMinimumResult = (new DoubleDiffuseSpace(3, 3)).findMinimum(2, 3, 1, false, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.MOORE);
        assertEquals(8, actualFindMinimumResult.length);
        assertEquals(0.0d, actualFindMinimumResult[0]);
        assertEquals(0.0d, actualFindMinimumResult[1]);
        assertEquals(0.0d, actualFindMinimumResult[2]);
        assertEquals(0.0d, actualFindMinimumResult[3]);
        assertEquals(0.0d, actualFindMinimumResult[4]);
        assertEquals(0.0d, actualFindMinimumResult[5]);
        assertEquals(0.0d, actualFindMinimumResult[6]);
        assertEquals(0.0d, actualFindMinimumResult[7]);
    }

    @Test
    void testFindMinimum12() {
        assertEquals(0, (new DoubleDiffuseSpace(3, 3)).findMinimum(2, 3, 0, false, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.MOORE).length);
    }

    @Test
    void testFindMinimum13() {
        double[] actualFindMinimumResult = (new DoubleDiffuseSpace(3, 3)).findMinimum(2, 1, 1, true, DoubleDiffuseSpace.NEIGHBOURHOOD_TYPE.VON_NEUMANN);
        assertEquals(5, actualFindMinimumResult.length);
        assertEquals(0.0d, actualFindMinimumResult[0]);
        assertEquals(0.0d, actualFindMinimumResult[1]);
        assertEquals(0.0d, actualFindMinimumResult[2]);
        assertEquals(0.0d, actualFindMinimumResult[3]);
        assertEquals(0.0d, actualFindMinimumResult[4]);
    }

    @Test
    void testGetMatrix() {
        double[] actualMatrix = (new DoubleDiffuseSpace(3, 3)).getMatrix();
        assertEquals(9, actualMatrix.length);
        assertEquals(0.0d, actualMatrix[0]);
        assertEquals(0.0d, actualMatrix[1]);
        assertEquals(0.0d, actualMatrix[2]);
        assertEquals(0.0d, actualMatrix[3]);
        assertEquals(0.0d, actualMatrix[4]);
        assertEquals(0.0d, actualMatrix[5]);
        assertEquals(0.0d, actualMatrix[6]);
        assertEquals(0.0d, actualMatrix[7]);
        assertEquals(0.0d, actualMatrix[8]);
    }
}

