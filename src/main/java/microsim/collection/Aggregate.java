package microsim.collection;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Aggregate {

    public static <T> void applyToFilter(Iterable<T> collection, Predicate<T> predicate, Consumer<T> closure) {
        collection.forEach(item -> {
            if (predicate.test(item)) {
                closure.accept(item);
            }
        });
    }

}
