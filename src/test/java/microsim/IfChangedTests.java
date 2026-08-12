package microsim;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class Foo {
    private double value = 0.0;

    public double value() {
        return this.value;
    }

    public void setValue(double newValue) {
        this.value = newValue;
    }
}

class IfChangedTests {
    @Test
    void ifChangedTest() {
        var foo = new Foo();
        var ifChanged = new IfChanged<>(foo::value);
        assertTrue(ifChanged.get());
        assertFalse(ifChanged.get());

        foo.setValue(1.0);
        assertTrue(ifChanged.get());
        assertFalse(ifChanged.get());

        foo.setValue(1.0);
        assertFalse(ifChanged.get());
    }
}
