package microsim.space;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AbstractSpaceTest {

    @Test
    void testBoundX() {
        assertEquals(2, (new DenseObjectSpace(3, 3)).boundX(2));
        assertEquals(1, (new DenseObjectSpace(2, 3)).boundX(2));
        assertEquals(0, (new DenseObjectSpace(3, 3)).boundX(-1));
    }

    @Test
    void testEquals() {
        assertNotEquals(new DenseObjectSpace(3, 3), null);
        assertNotEquals(new DenseObjectSpace(3, 3), "Different type to AbstractSpace");
    }

    @Test
    void testEquals2() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        assertEquals(denseObjectSpace, denseObjectSpace);
        int expectedHashCodeResult = denseObjectSpace.hashCode();
        assertEquals(expectedHashCodeResult, denseObjectSpace.hashCode());
    }

    @Test
    void testEquals3() {
        DenseObjectSpace denseObjectSpace = new DenseObjectSpace(3, 3);
        assertNotEquals(denseObjectSpace, new DenseObjectSpace(3, 3));
    }
}

