package microsim.collection;

import lombok.NonNull;
import lombok.val;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * A utility class for aggregation tools.
 */
public class Aggregate {

    /**
     * This method filters out certain objects from the provided collection according to the predicate and applies a
     * function to them.
     *
     * @param collection A collection of objects.
     * @param predicate  A logical predicate to filter the collection.
     * @param closure    A function to be applied to all filtered objects.
     * @param <T>        A generic type.
     */
    public static <T> void applyToFilter(final @Nullable Iterable<T> collection, final @Nullable Predicate<T> predicate,
                                         final @NonNull Closure<T> closure) {
        val filtered = new ArrayList<T>();
        CollectionUtils.select(collection, predicate, filtered);
        IterableUtils.forEach(filtered, closure);
    }
}
