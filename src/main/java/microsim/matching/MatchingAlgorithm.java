package microsim.matching;

import lombok.NonNull;
import org.apache.commons.collections4.Predicate;

import java.util.Collection;
import java.util.Comparator;

public interface MatchingAlgorithm<T> {

    void matching(final @NonNull Collection<T> collection1, final @NonNull Predicate<T> filter1,
                  final @NonNull Comparator<T> comparator1, final @NonNull Collection<T> collection2,
                  final @NonNull Predicate<T> filter2, final @NonNull MatchingScoreClosure<T> doubleClosure,
                  final @NonNull MatchingClosure<T> matching);

}
