package microsim.collection;

import jamjam.Sum;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.*;

class AverageClosureTest {

    static class A extends AverageClosure<Double> {
        @Override
        public void execute(Double aDouble) {
        }
    }

    @Test
    void getAverage() {
        val mock = new A();
        assertEquals(-0.d, mock.getSum());
        mock.add(new double[]{1, 2, 3});
        assertEquals(2.d, mock.getAverage());
    }

    @Test
    void testAdd() {
        val mock = new A();
        assertEquals(-0.d, mock.getSum());
        mock.add(1.);
        assertEquals(1.d, mock.getSum());
    }

    @Test
    void testAdd1() {
        val mock = new A();
        assertEquals(-0.d, mock.getSum());
        mock.add(new double[]{1, 2, 3});
        assertEquals(6.d, mock.getSum());
    }

    @Test
    void testAdd2() {
        val mock = new A();
        assertThrows(NullPointerException.class, () -> mock.add((double[]) null));
    }

    @Test
    void testAdd3() {
        val mock = new A();
        assertEquals(-0.d, mock.getSum());
        val s = Arrays.stream(new double[]{1, 2, 3});
        mock.add(s);
        assertEquals(6.d, mock.getSum());
    }

    @Test
    void testAdd4() {
        val mock = new A();
        assertThrows(NullPointerException.class, () -> mock.add((DoubleStream) null));
    }

    @Test
    void testGetAccumulator() {
        val mock = new A();
        assertInstanceOf(Sum.Accumulator.class, mock.getAccumulator());
    }

    @Test
    void testGetCount() {
        val mock = new A();
        assertEquals(0, mock.getCount());
        mock.add(1.);
        assertEquals(1, mock.getCount());
    }
}
