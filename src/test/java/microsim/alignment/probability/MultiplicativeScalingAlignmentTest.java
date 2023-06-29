package microsim.alignment.probability;

import org.apache.commons.collections4.Predicate;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class MultiplicativeScalingAlignmentTest {

    @Test
    void testAlign() {
        MultiplicativeScalingAlignment<Object> multiplicativeScalingAlignment = new MultiplicativeScalingAlignment<>();
        assertThrows(NullPointerException.class, () -> multiplicativeScalingAlignment.align(new ArrayList<>(),
            (Predicate<Object>) mock(Predicate.class), null, 0.25d));
    }

    @Test
    void testAlign1() {
        assertThrows(NullPointerException.class, () -> (new MultiplicativeScalingAlignment<>()).align(null,
            (Predicate<Object>) mock(Predicate.class), null, 10.0d));
    }
}

