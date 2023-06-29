package microsim.matching;

import lombok.NonNull;

public interface MatchingScoreClosure<T> {

    double getValue(final @NonNull T item1, final @NonNull T item2);

}
