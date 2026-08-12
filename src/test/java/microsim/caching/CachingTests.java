package microsim.caching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import microsim.IfChanged;

class Foo {
    private int value = 0;

    public int value() {
        return this.value;
    }

    public int incrementAndGet() {
        this.value += 1;
        return this.value;
    }
}

class CachingTests {
    @Test
    void onceTest() {
        // making sure a bare `Foo` behaves as expected.
        var fooCheck = new Foo();
        assertEquals(1, fooCheck.incrementAndGet());
        assertEquals(2, fooCheck.incrementAndGet());

        var foo = new Foo();
        var once = new Once<>(foo::incrementAndGet);
        assertEquals(1, once.get());
        assertEquals(1, once.get());
        assertEquals(2, foo.incrementAndGet());
        assertEquals(1, once.get());
    }

    @Test
    void onceUntil() {
        var foo = new Foo();
        var bar = new Foo();
        var once = new OnceUntil<>(bar::incrementAndGet, new IfChanged<>(foo::value));

        assertEquals(1, once.get());
        assertEquals(1, once.get());
        assertEquals(1, foo.incrementAndGet());
        assertEquals(2, once.get());
        assertEquals(2, once.get());
    }
}
