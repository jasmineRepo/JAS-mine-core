package microsim.caching;

import java.util.Objects;
import java.util.function.Supplier;

/// Cache the result of a [Supplier] so that it is only called once.
///
/// This requires the wrapped [Supplier] to return a non-null object. This is
/// lazy in the sense that the wrapped [Supplier] is only called when the value
/// is requested for the first time.
public class Once<T> implements Supplier<T> {
    private final Supplier<? extends T> supplier;
    private T value;

    /// Build a cache wrapping the given [Supplier].
    ///
    /// Note that wrapping a [Once] automatically flattens it.
    public Once(Supplier<? extends T> supplier) {
        if (supplier instanceof Once<? extends T> once) {
            this.supplier = once.supplier;
            this.value = once.value;
        } else {
            this.supplier = supplier;
            this.value = null;
        }
    }

    /// Get the value.
    ///
    /// @throws NullPointerException if the wrapped [Supplier] returns `null`.
    @Override
    public T get() {
        if (this.value == null) {
            this.value = Objects.requireNonNull(this.supplier.get());
        }
        return this.value;
    }
}
